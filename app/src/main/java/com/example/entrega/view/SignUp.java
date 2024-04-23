package com.example.entrega.view;

import static androidx.core.content.ContextCompat.startActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.entrega.R;
import com.example.entrega.controller.RemoteDBHandler;

import java.util.Objects;

public class SignUp extends AppCompatActivity {
    private EditText edtDNI, edtPassword, edtPassword2, edtName, edtLastName, edtPhone;
    private Button btnSignUpSubmit;
    private Switch swNotifications;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);


        edtDNI = findViewById(R.id.idEdtDNI);
        edtPassword = findViewById(R.id.idEdtContrasena);
        edtPassword2 = findViewById(R.id.idEdtContrasena2);
        edtName = findViewById(R.id.idEdtName);
        edtLastName = findViewById(R.id.idEdtLastName);
        edtPhone = findViewById(R.id.idEdtTelephone);
        btnSignUpSubmit = findViewById(R.id.idBtnSignUpSubmit);
        swNotifications = findViewById(R.id.notifications);


        btnSignUpSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registrarse();
            }
        });
    }

    public void registrarse() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                String dni = edtDNI.getText().toString();
                String contrasena = edtPassword.getText().toString();
                String contrasena2 = edtPassword2.getText().toString();
                String name = edtName.getText().toString();
                String lastName = edtLastName.getText().toString();
                String phone = edtPhone.getText().toString();
                Boolean notifications = swNotifications.isChecked();

                SharedPreferences prefs = getSharedPreferences("my_preferences", MODE_PRIVATE);
                String token = prefs.getString("fcm_token","eee");

                if (dni.isEmpty() || contrasena.isEmpty() || contrasena2.isEmpty() || name.isEmpty() || lastName.isEmpty() || phone.isEmpty()){
                    SignUp.this.runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(SignUp.this, "Please enter all the data..", Toast.LENGTH_SHORT).show();
                        }
                    });
                    return;
                }
                if (!contrasena.equals(contrasena2)){
                    SignUp.this.runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(SignUp.this, "Passwords do not match.", Toast.LENGTH_SHORT).show();
                        }
                    });
                    return;
                }
                int code = RemoteDBHandler.registerUser(dni, contrasena, name, lastName, phone, notifications, token);

                if (code == 200) {
                    SignUp.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast toast = Toast.makeText(SignUp.this, "User registered succesfully!", Toast.LENGTH_SHORT);
                            toast.show();
                        }
                    });

                    // After sing up we move to the login screen
                    startActivity(new Intent(SignUp.this, LogIn.class));
                } else {
                    SignUp.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast toast = Toast.makeText(SignUp.this, "No se logró crear el usuario", Toast.LENGTH_SHORT);
                            toast.show();
                        }
                    });
                }
            }
        });

        // Check if the thread have been already created
        if (thread.isAlive()) {
            thread.interrupt();
        }

        thread.start();
    }
}
