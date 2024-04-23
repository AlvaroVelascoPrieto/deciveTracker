package com.example.entrega.view;

import static androidx.core.content.ContextCompat.startActivity;

import static com.example.entrega.controller.RemoteDBHandler.checkUserCreds;
import static com.example.entrega.controller.RemoteDBHandler.editProfileField;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import com.example.entrega.R;
import com.example.entrega.controller.RemoteDBHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;
import java.util.Objects;

public class LogIn extends AppCompatActivity {
    private EditText edtDNI, edtPassword;
    private Button btnLogIn, btnSignUp;
    String ip;
    static final int  ACCESS_BACKGROUND_LOCATION = 98;
    private static final String DEFAULT_LANGUAGE = "en";


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setLocale();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        Objects.requireNonNull(getSupportActionBar()).setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        androidx.appcompat.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setIcon(R.drawable.ic_launcher);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

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

        if (this.checkCallingOrSelfPermission("android.permission.ACCESS_BACKGROUND_LOCATION") == PackageManager.PERMISSION_GRANTED){

        }
        else{
            if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.M){
                requestPermissions (new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION}, ACCESS_BACKGROUND_LOCATION);
            }
        }
    }

    //Handling the login with multithreading
    private void logIn() {
        SharedPreferences prefs = getSharedPreferences("my_preferences", MODE_PRIVATE);

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
                    String notificaciones = data.getString("notificaciones");
                    String token = prefs.getString("fcm_token","eee");
                    editProfileField(dni, contrasena, nombre, apellido, telefono, Boolean.getBoolean(notificaciones), token);
                    // Passing data to another Activity
                    Intent i = new Intent(LogIn.this, ActionMenu.class);
                    i.putExtra("id", dni);
                    i.putExtra("password", contrasena);
                    i.putExtra("name", nombre);
                    i.putExtra("lastname", apellido);
                    i.putExtra("phone", telefono);
                    i.putExtra("notificaciones", notificaciones);
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

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }




        });

        if (thread.isAlive()) {
            // Ending thread after there was a successful login
            thread.interrupt();
        }

        thread.start();
    }

    //Method to create the toolbar menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    // Method to handle clicks on toolbar items
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_settings) {
            Intent i = new Intent(LogIn.this, Preferences.class);
            startActivity(i);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //Setting language preferences
    private void setLocale() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (!preferences.contains("selected_language")) {
            preferences.edit().putString("selected_language", DEFAULT_LANGUAGE).apply();
        }
        String selectedLanguage = preferences.getString("selected_language", DEFAULT_LANGUAGE);
        Locale locale = new Locale(selectedLanguage);
        Locale.setDefault(locale);
        Resources resources = getResources();
        Configuration configuration = resources.getConfiguration();
        configuration.setLocale(locale);
        resources.updateConfiguration(configuration, resources.getDisplayMetrics());
    }


}

