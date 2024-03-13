package com.example.entrega;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.entrega.controller.DBHandler;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    // creating variables for our edittext, button and dbhandler
    private EditText landmarkNameEdt, logDateEdt, longitudeEdt, latitudeEdt, altitudeEdt;
    private Spinner locationTypeSpinner;
    private Button addDeviceBtn, seeDevicesBtn, getGetLocationButton;
    private DBHandler dbHandler;
    public static final int DEFAULT_UPDATE_INTERVAL = 30;
    public static final int MAXIMUM_UPDATE_INTERVAL = 5;
    static final int PERMISSIONS_FINE_LOCATION = 99;
    //Google's API for location services
    FusedLocationProviderClient fusedLocationProviderClient;
    LocationRequest locationRequest;
    TextView latitudeTV, altitudeTV, accuracyTV, addressTV;

    Button getLocButton;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // initializing all our variables.
        landmarkNameEdt = findViewById(R.id.idEdtLandmarkName);
        logDateEdt = findViewById(R.id.idEdtlogDate);
        longitudeEdt = findViewById(R.id.idEdtLongitude);
        latitudeEdt = findViewById(R.id.idEdtLatitude);
        altitudeEdt = findViewById(R.id.idEdtAltitude);
        getGetLocationButton = findViewById(R.id.idBtnGetLocation);
        locationTypeSpinner = findViewById(R.id.idSpinerDeviceType);
        addDeviceBtn = findViewById(R.id.idBtnAddDevice);
        seeDevicesBtn = findViewById(R.id.idBtnSeeDevices);

        // Spinner Drop down elements
        List<String> categories = new ArrayList<String>();
        categories.add("Landmark");
        categories.add("Views");
        categories.add("Entertainment");
        categories.add("Home");
        categories.add("Work");

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        locationTypeSpinner.setAdapter(dataAdapter);


        // creating a new dbhandler class
        // and passing our context to it.
        dbHandler = new DBHandler(MainActivity.this);


        logDateEdt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker(MainActivity.this);
            }
        });
        // below line is to add on click listener for our add course button.
        addDeviceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // below line is to get data from all edit text fields.
                String landmarkName = landmarkNameEdt.getText().toString();
                String logDate = logDateEdt.getText().toString();
                String latitude = latitudeEdt.getText().toString();
                String longitude = longitudeEdt.getText().toString();
                String altitude = altitudeEdt.getText().toString();
                String locationType = locationTypeSpinner.getSelectedItem().toString();

                // Notification Channel ID. You can create multiple channels if needed.
                String channelId = "my_channel_id";
                CharSequence channelName = "My Channel";

                NotificationManager notificationManager =
                        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                // Check if the channel exists (required for API 26 and above)
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    NotificationChannel channel = notificationManager.getNotificationChannel(channelId);
                    if (channel == null) {
                        // Create the channel if it doesn't exist
                        channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT);
                        // Configure the channel's behavior
                        channel.setDescription("My Channel Description");
                        // Register the channel with the system
                        notificationManager.createNotificationChannel(channel);
                    }
                }

                // Now, you can create and show notifications using this channel
                NotificationCompat.Builder builder = new NotificationCompat.Builder(MainActivity.this, channelId)
                        .setSmallIcon(R.drawable.ic_launcher_foreground)
                        .setContentTitle("Lugar añadido")
                        .setContentText(landmarkName);

                // Show the notification
                notificationManager.notify(1, builder.build());

                // validating if the text fields are empty or not.
                if (landmarkName.isEmpty() || logDate.isEmpty() || longitude.isEmpty() || latitude.isEmpty() || altitude.isEmpty() || locationType.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Please enter all the data..", Toast.LENGTH_SHORT).show();
                    return;
                }

                // on below line we are calling a method to add new
                // course to sqlite data and pass all our values to it.
                dbHandler.addNewLocation(landmarkName, logDate, longitude, latitude, altitude, locationType);

                // after adding the data we are displaying a toast message.
                Toast.makeText(MainActivity.this, "Place has been added.", Toast.LENGTH_SHORT).show();
                landmarkNameEdt.setText("");
                longitudeEdt.setText("");
                latitudeEdt.setText("");
                altitudeEdt.setText("");
                logDateEdt.setText("");
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("My channel", "MyNotificationChannel", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

        seeDevicesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // opening a new activity via a intent.
                Intent i = new Intent(MainActivity.this, ViewDevices.class);
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
    }
    private void showDatePicker(Context context) {
        // Get current year, month, and day
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);

        // Create DatePickerDialog and set the selected date listener
        DatePickerDialog datePickerDialog = new DatePickerDialog(context,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        // Update the EditText with the selected year
                        String date = String.valueOf(year) + "/" + String.valueOf(month) + "/" + String.valueOf(dayOfMonth);
                        logDateEdt.setText(date);
                    }
                }, year, calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        // Set the maximum date to the current date
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());

        // Show the DatePickerDialog
        datePickerDialog.show();
    }

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

    private void updateGPS(){
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        if (this.checkCallingOrSelfPermission("android.permission.ACCESS_FINE_LOCATION") == PackageManager.PERMISSION_GRANTED){
            //User provided permissions
            //Get last location (not current) last stored one
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<android.location.Location>() {
                @Override
                public void onSuccess(android.location.Location location) {
                    //We got the location we have to update
                    updateUI(location);
                }
            });
        }
        else{
            if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.M){
                requestPermissions (new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_FINE_LOCATION);
            }
        }
    }

    private void updateUI(android.location.Location location){
        latitudeEdt.setText(String.valueOf(location.getLatitude()));
        longitudeEdt.setText(String.valueOf(location.getLongitude()));

        if (location.hasAltitude()){
            altitudeEdt.setText(String.valueOf(location.getAltitude()));
        }
        else{
            altitudeEdt.setText("Not Available");
        }

    }
}
