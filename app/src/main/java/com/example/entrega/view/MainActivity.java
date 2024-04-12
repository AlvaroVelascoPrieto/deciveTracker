package com.example.entrega.view;

import static android.Manifest.permission.ACCESS_BACKGROUND_LOCATION;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.core.app.NotificationCompat;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.example.entrega.R;
import com.example.entrega.controller.DBHandler;
import com.example.entrega.model.MyWorker;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    private static final String DEFAULT_LANGUAGE = "en";
    private EditText landmarkNameEdt, logDateEdt, longitudeEdt, latitudeEdt, altitudeEdt;
    private Spinner locationTypeSpinner;
    private DBHandler dbHandler;
    private Button getGetLocationButton, addLocationBtn, seeLocationsBtn;
    //Google's API for location services
    FusedLocationProviderClient fusedLocationProviderClient;
    LocationRequest locationRequest;
    public static final int DEFAULT_UPDATE_INTERVAL = 30;
    public static final int MAXIMUM_UPDATE_INTERVAL = 5;
    static final int PERMISSIONS_FINE_LOCATION = 99;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Set the saved language
        setLocale();
        super.onCreate(savedInstanceState);

        PeriodicWorkRequest periodicWork = new PeriodicWorkRequest.Builder(MyWorker.class, 15, TimeUnit.MINUTES)
                .build();
        WorkManager.getInstance().enqueueUniquePeriodicWork("Location", ExistingPeriodicWorkPolicy.REPLACE, periodicWork);

        setContentView(R.layout.activity_main);

        //Action bar configuration
        Objects.requireNonNull(getSupportActionBar()).setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        androidx.appcompat.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setIcon(R.drawable.ic_launcher);
        actionBar.setTitle("Location Tracker");
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        // initializing all our variables.
        landmarkNameEdt = findViewById(R.id.idEdtLandmarkName);
        logDateEdt = findViewById(R.id.idEdtlogDate);
        longitudeEdt = findViewById(R.id.idEdtLongitude);
        latitudeEdt = findViewById(R.id.idEdtLatitude);
        altitudeEdt = findViewById(R.id.idEdtAltitude);
        getGetLocationButton = findViewById(R.id.idBtnGetLocation);
        locationTypeSpinner = findViewById(R.id.idSpinnerDeviceType);
        addLocationBtn = findViewById(R.id.idBtnAddLocation);
        seeLocationsBtn = findViewById(R.id.idBtnSeeLocations);

        // Spinner Drop down elements, adapter and style
        List<String> categories = new ArrayList<String>();
        categories.add("Landmark");
        categories.add("Views");
        categories.add("Entertainment");
        categories.add("Home");
        categories.add("Work");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        locationTypeSpinner.setAdapter(dataAdapter);

        dbHandler = new DBHandler(MainActivity.this);

        //Define on click listeners for the buttons
        logDateEdt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker(MainActivity.this);
            }
        });


        addLocationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String landmarkName = landmarkNameEdt.getText().toString();
                String logDate = logDateEdt.getText().toString();
                String latitude = latitudeEdt.getText().toString();
                String longitude = longitudeEdt.getText().toString();
                String altitude = altitudeEdt.getText().toString();
                String locationType = locationTypeSpinner.getSelectedItem().toString();

                //Check completion
                if (landmarkName.isEmpty() || logDate.isEmpty() || longitude.isEmpty() || latitude.isEmpty() || altitude.isEmpty() || locationType.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Please enter all the data..", Toast.LENGTH_SHORT).show();
                    return;
                }

                //Send notification
                pushNotification(landmarkName);

                //Add location to database
                dbHandler.addNewLocation(landmarkName, logDate, longitude, latitude, altitude, locationType);

                //Display Tosts indicating insertion and clear fields
                Toast.makeText(MainActivity.this, "Place has been added.", Toast.LENGTH_SHORT).show();
                landmarkNameEdt.setText("");
                longitudeEdt.setText("");
                latitudeEdt.setText("");
                altitudeEdt.setText("");
                logDateEdt.setText("");
            }
        });

        seeLocationsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // opening a new activity via a intent.
                Intent i = new Intent(MainActivity.this, ViewLocations.class);
                startActivity(i);
            }
        }
        );

        //Location request settings
        locationRequest = new LocationRequest();
        locationRequest.setInterval(1000 * DEFAULT_UPDATE_INTERVAL);
        locationRequest.setFastestInterval(1000 * MAXIMUM_UPDATE_INTERVAL);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        getGetLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateGPS();
            }
        });

        //Notification channel settings
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("My channel", "MyNotificationChannel", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }

    //Methot to send a notification
    private void pushNotification(String landmarkName) {
        String channelId = "my_channel_id";
        CharSequence channelName = "My Channel";

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = notificationManager.getNotificationChannel(channelId);
            if (channel == null) {
                channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT);
                channel.setDescription("My Channel Description");
                notificationManager.createNotificationChannel(channel);
            }
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(MainActivity.this, channelId)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Lugar añadido")
                .setContentText(landmarkName);

        notificationManager.notify(1, builder.build());
    }

    //Method to set preferences to configure language
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
            Intent i = new Intent(MainActivity.this, Preferences.class);
            startActivity(i);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //Method to show calendar date picker
    private void showDatePicker(Context context) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);

        DatePickerDialog datePickerDialog = new DatePickerDialog(context,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        // Update the EditText with the selected year
                        String date = String.valueOf(year) + "/" + String.valueOf(month) + "/" + String.valueOf(dayOfMonth);
                        logDateEdt.setText(date);
                    }
                }, year, calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        datePickerDialog.show();
    }

    //Method to request location usage permission
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case PERMISSIONS_FINE_LOCATION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    updateGPS();
                }
                else{
                    Toast.makeText(this, "Location permission not granted", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
        }
    }

    //Method to update GPS Coordinates
    private void updateGPS(){
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        if (this.checkCallingOrSelfPermission("android.permission.ACCESS_FINE_LOCATION") == PackageManager.PERMISSION_GRANTED){
            //User provided permissions
            //Get last location (not current) last stored one
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<android.location.Location>() {
                @Override
                public void onSuccess(android.location.Location location) {
                    latitudeEdt.setText(String.valueOf(location.getLatitude()));
                    longitudeEdt.setText(String.valueOf(location.getLongitude()));

                    if (location.hasAltitude()){
                        altitudeEdt.setText(String.valueOf(location.getAltitude()));
                    }
                    else{
                        altitudeEdt.setText("Not Available");
                    }
                }
            });
        }
        else{
            if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.M){
                requestPermissions (new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_FINE_LOCATION);
            }
        }
    }
}
