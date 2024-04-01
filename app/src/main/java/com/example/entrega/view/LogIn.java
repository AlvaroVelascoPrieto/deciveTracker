package com.example.entrega.view;

import static androidx.core.content.ContextCompat.startActivity;

import static com.example.entrega.controller.RemoteDBHandler.checkUserCreds;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.entrega.R;
import com.example.entrega.controller.RemoteDBHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;

public class LogIn extends AppCompatActivity {
    private EditText edtDNI, edtPassword;
    private Button btnLogIn, btnSignUp;
    String ip;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);


        edtDNI = findViewById(R.id.idEdtDNI);
        edtPassword =  findViewById(R.id.idEdtContrasena);
        btnLogIn = findViewById(R.id.idBtnLogIn);
        btnSignUp = findViewById(R.id.idBtnSignUp);

        btnLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logIn();
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LogIn.this, SignUp.class);
                startActivity(i);
            }
        });
    }

    private void logIn() {
        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {

                String dni = edtDNI.getText().toString();
                String contrasena = edtPassword.getText().toString();
                if (dni.isEmpty() || contrasena.isEmpty()) {
                    LogIn.this.runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(LogIn.this, "Please enter all the data..", Toast.LENGTH_SHORT).show();
                        }
                    });
                    return;
                }
                JSONObject info = checkUserCreds(dni, contrasena);
                if (info==null) {
                    LogIn.this.runOnUiThread(new Runnable() {
                        public void run() {
                            Toast toast = Toast.makeText(LogIn.this, "Incorrect credentials!", Toast.LENGTH_SHORT);
                            toast.show();
                        }
                    });
                    return;
                }

                try{
                    JSONObject data = info.getJSONObject("data");
                    String nombre = data.getString("nombre");
                    String apellido = data.getString("apellido");
                    String telefono = data.getString("telefono");
                    // Passing data to another Activity
                    Intent i = new Intent(LogIn.this, MainActivity.class);
                    i.putExtra("dni", dni);
                    i.putExtra("contrasena", contrasena);
                    i.putExtra("nombre", nombre);
                    i.putExtra("apellido", apellido);
                    i.putExtra("telefono", telefono);
                    LogIn.this.runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(LogIn.this, "Welcome", Toast.LENGTH_SHORT).show();
                        }
                    });

                    startActivity(i);
                }catch (JSONException e){
                    LogIn.this.runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(LogIn.this, "Ha ocurrido un error!", Toast.LENGTH_SHORT).show();
                        }
                    });

                }
                }




        });

        if (thread.isAlive()) {
            // Ending thread after there was a successful login
            thread.interrupt();
        }

        thread.start();
    }


}

