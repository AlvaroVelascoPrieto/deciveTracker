package com.example.entrega.view;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;

import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.entrega.R;
import com.example.entrega.controller.DBHandler;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class UpdateDeviceActivity extends AppCompatActivity {

    private static final int PERMISSIONS_FINE_LOCATION = 99;
    // variables for our edit text, button, strings and dbhandler class.
    private EditText locationNameEdt, logDateEdt, latitudeEdt, longitudeEdt, altitudeEdt;
    private Spinner locationTypeSpinner;
    private DBHandler dbHandler;
    String locationName, longitude, latitude, altitude, logDate, locationType;
    FusedLocationProviderClient fusedLocationProviderClient;
    LocationRequest locationRequest;



    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_device);

        // Initializing toolbar



        // initializing all our variables.
        locationNameEdt = findViewById(R.id.idEdtLandmarkName);
        logDateEdt = findViewById(R.id.idEdtlogDate);
        latitudeEdt = findViewById(R.id.idEdtLatitude);
        longitudeEdt = findViewById(R.id.idEdtLongitude);
        altitudeEdt = findViewById(R.id.idEdtAltitude);
        Button getLocation = findViewById(R.id.idBtnGetLocation);
        locationTypeSpinner = findViewById(R.id.idSpinerDeviceType);
        Button updateLocationBtn = findViewById(R.id.idBtnUpdateDevice);
        Button deleteLocationeBtn = findViewById(R.id.idBtnDelete);
        Button openInMaps = findViewById(R.id.idBtnOpenMap);


        // on below line we are initializing our dbhandler class.
        dbHandler = new DBHandler(UpdateDeviceActivity.this);

        // on below lines we are getting data which
        // we passed in our adapter class.
        locationName = getIntent().getStringExtra("name");
        locationType = getIntent().getStringExtra("type");
        latitude = getIntent().getStringExtra("latitude");
        longitude = getIntent().getStringExtra("longitude");
        altitude = getIntent().getStringExtra("altitude");
        logDate = getIntent().getStringExtra("date");

        // setting data to edit text
        // of our update activity.
        locationNameEdt.setText(locationName);
        logDateEdt.setText(logDate);
        latitudeEdt.setText(latitude);
        longitudeEdt.setText(longitude);
        altitudeEdt.setText(altitude);


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
        locationTypeSpinner.setSelection(dataAdapter.getPosition(locationType));

        // adding on click listener to our update device button.
        updateLocationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // inside this method we are calling an update device
                // method and passing all our edit text values.
                dbHandler.updateDevice(locationName, locationNameEdt.getText().toString(), latitudeEdt.getText().toString(), longitudeEdt.getText().toString(), altitudeEdt.getText().toString(), locationTypeSpinner.getSelectedItem().toString(), logDateEdt.getText().toString());

                // displaying a toast message that our device has been updated.
                Toast.makeText(UpdateDeviceActivity.this, "Device Updated..", Toast.LENGTH_SHORT).show();

                // launching our main activity.
                Intent i = new Intent(UpdateDeviceActivity.this, MainActivity.class);
                startActivity(i);
            }
        });

        logDateEdt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker(UpdateDeviceActivity.this);
            }
        });

        getLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateGPS();
            }
        });

        // adding on click listener for delete button to delete our device.
        deleteLocationeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create the object of AlertDialog Builder class
                AlertDialog.Builder builder = new AlertDialog.Builder(UpdateDeviceActivity.this);

                // Set the message show for the Alert time
                builder.setMessage("Do you really want to delete this device?");

                // Set Alert Title
                builder.setTitle("Delete device");

                // Set Cancelable false for when the user clicks on the outside the Dialog Box then it will remain show
                builder.setCancelable(false);

                // Set the positive button with yes name Lambda OnClickListener method is use of DialogInterface interface.
                builder.setPositiveButton("Yes", (DialogInterface.OnClickListener) (dialog, which) -> {
                    // When the user click yes button then app will close
                    dbHandler.deleteDevice(locationName);
                    Toast.makeText(UpdateDeviceActivity.this, "Deleted the device", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(UpdateDeviceActivity.this, MainActivity.class);
                    startActivity(i);
                });

                // Set the Negative button with No name Lambda OnClickListener method is use of DialogInterface interface.
                builder.setNegativeButton("No", (DialogInterface.OnClickListener) (dialog, which) -> {
                    // If user click no then dialog box is canceled.
                    dialog.cancel();
                });

                // Create the Alert dialog
                AlertDialog alertDialog = builder.create();
                // Show the Alert Dialog box
                alertDialog.show();
                // calling a method to delete our device.

            }
        });

        openInMaps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String uri = String.format(Locale.ENGLISH, "geo:%f,%f", Float.valueOf(latitude), Float.valueOf(longitude));
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                startActivity(intent);
            }
        });
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
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
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


    // Method to display DatePickerDialog
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
                        logDateEdt.setText(String.valueOf(year));
                    }
                }, year, calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        // Set the maximum date to the current date
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());

        // Show the DatePickerDialog
        datePickerDialog.show();
    }

    // Method to create the toolbar menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.actionbar, menu);
        return true;
    }

    // Method to handle clicks on toolbar items
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_share) {
            // Handle the share action here
            shareDeviceData();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Method to share device data
    private void shareDeviceData() {
        // Get the device data
        String locationName = locationNameEdt.getText().toString();
        String locationDate = logDateEdt.getText().toString();
        String latitude = latitudeEdt.getText().toString();
        String longitude = longitudeEdt.getText().toString();
        String altitude = altitudeEdt.getText().toString();
        String locationType = locationTypeSpinner.getSelectedItem().toString();

        // Create the share intent
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Device Information");
        shareIntent.putExtra(Intent.EXTRA_TEXT, "Location Name: " + locationName +
                "\nLocation Date: " + locationDate +
                "\nLatitude: " + latitude +
                "\nLongitude: " + longitude +
                "\nAltitude: " + altitude +
                "\nLocation Type: " + locationType);

        // Start the share activity
        startActivity(Intent.createChooser(shareIntent, "Share Device Data"));
    }
}
