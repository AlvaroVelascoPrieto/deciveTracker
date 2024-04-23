package com.example.entrega.view;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.OnBackPressedDispatcherOwner;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.entrega.R;
import com.example.entrega.controller.RemoteDBHandler;

public class ProfileActivity extends AppCompatActivity {
    private TextView clickedTextView;
    private String id, password, name, lastname, phone, notificaciones;
    private TextView edtId, edtPassword, edtName, edtLastName, edtPhone;

    private ImageView profilePicture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);
        id = getIntent().getStringExtra("id");
        password = getIntent().getStringExtra("password");
        name = getIntent().getStringExtra("name");
        lastname = getIntent().getStringExtra("lastname");
        phone = getIntent().getStringExtra("phone");
        notificaciones = getIntent().getStringExtra("notificaciones");
        profilePicture = findViewById(R.id.profile_picture);
        edtId = findViewById(R.id.user_id_value);
        edtId.setText(id);
        edtPassword = findViewById(R.id.password_value);
        edtPassword.setText(password);
        edtName = findViewById(R.id.name_value);
        edtName.setText(name);
        edtLastName = findViewById(R.id.last_name_value);
        edtLastName.setText(lastname);
        edtPhone = findViewById(R.id.phone_value);
        edtPhone.setText(phone);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        // This callback is only called when MyFragment is at least started
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                Intent i = new Intent(ProfileActivity.this, ActionMenu.class);
                i.putExtra("id", id);
                i.putExtra("password", password);
                i.putExtra("name", name);
                i.putExtra("lastname", lastname);
                i.putExtra("phone", phone);
                i.putExtra("notificaciones", notificaciones);
                startActivity(i);
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);

        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                Bitmap bm = RemoteDBHandler.getUserProfilePicture(id);
                System.out.println("BITMAP");
                System.out.println(bm);
                ProfileActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        profilePicture.setImageBitmap(bm);
                        profilePicture.setRotation(90);
                    }
                });
            }

        });

        if (thread.isAlive()) {
            thread.interrupt();
        }
        thread.start();
    }

    public void changeProfilePicture(View view) {
        Intent i = new Intent(ProfileActivity.this, CameraActivity.class);
        i.putExtra("id", id);
        startActivity(i);
    }
    public void changePassword(View view) {
        showDialog((TextView) view);
    }

    public void changeName(View view) {
        showDialog((TextView) view);
    }

    public void changeLastName(View view) {
        showDialog((TextView) view);
    }

    public void changePhone(View view) {
        showDialog((TextView) view);
    }

    //Showing dialog to change data
    private void showDialog(TextView textView) {
        clickedTextView = textView;
        String currentValue = ((TextView) findViewById(textView.getId())).getText().toString();

        ChangeInfoDialogFragment dialog = ChangeInfoDialogFragment.newInstance(currentValue);
        dialog.show(getSupportFragmentManager(), "ChangeInfoDialogFragment");

    }

    //Handling database update when info changes
    public void onInfoChanged(String newInfo) {
        // Update the clicked text view with the new information
        if (clickedTextView != null) {
            clickedTextView.setText(newInfo);
            Thread thread = new Thread(new Runnable() {

                @Override
                public void run() {
                    try {
                        SharedPreferences prefs = getSharedPreferences("my_preferences", MODE_PRIVATE);
                        String id = edtId.getText().toString();
                        String nPassword = edtPassword.getText().toString();
                        String nName = edtName.getText().toString();
                        String nLastName = edtLastName.getText().toString();
                        String nPhone = edtPhone.getText().toString();
                        String token = prefs.getString("fcm_token","eee");

                        int code = RemoteDBHandler.editProfileField(id, nPassword, nName, nLastName, nPhone, Boolean.getBoolean(notificaciones), token);

                        if (code == 200) {
                            ProfileActivity.this.runOnUiThread(new Runnable() {
                                public void run() {
                                    password = nPassword;
                                    name = nName;
                                    lastname = nLastName;
                                    phone = nPhone;

                                    Toast toast = Toast.makeText(ProfileActivity.this, "Se han efectuado los cambios con éxito!", Toast.LENGTH_SHORT);
                                    toast.show();
                                }
                            });
                        } else {
                            // Rollback the data to the original
                            ProfileActivity.this.runOnUiThread(new Runnable() {
                                public void run() {
                                    Toast toast = Toast.makeText(ProfileActivity.this, "No se lograron efectuar los cambios.", Toast.LENGTH_SHORT);
                                    toast.show();
                                }
                            });
                        }

                    } catch (Exception e) {
                        // Rollback the data to the original
                        ProfileActivity.this.runOnUiThread(new Runnable() {
                            public void run() {
                                edtId.setText(id);
                                edtPassword.setText(password);
                                edtName.setText(name);
                                edtLastName.setText(lastname);
                                edtPhone.setText(phone);
                                Toast toast = Toast.makeText(ProfileActivity.this, "No se lograron efectuar los cambios.", Toast.LENGTH_SHORT);
                                toast.show();
                            }
                        });
                        Log.d("Error", "Ocurrió un error al intentar actualizar los datos del usuario");
                        Log.d("Error", e.toString());
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

    //Handling back button
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent i = new Intent(ProfileActivity.this, ActionMenu.class);
                i.putExtra("id", id);
                i.putExtra("password", password);
                i.putExtra("name", name);
                i.putExtra("lastname", lastname);
                i.putExtra("phone", phone);
                startActivity(i);

        }
        return super.onOptionsItemSelected(item);
    }
}
