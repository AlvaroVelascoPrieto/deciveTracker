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
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class UpdateLocationActivity extends AppCompatActivity {
    private static final int PERMISSIONS_FINE_LOCATION = 99;
    private EditText locationNameEdt, logDateEdt, latitudeEdt, longitudeEdt, altitudeEdt;
    private Spinner locationTypeSpinner;
    private DBHandler dbHandler;
    String locationName, longitude, latitude, altitude, logDate, locationType;
    FusedLocationProviderClient fusedLocationProviderClient;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_device);

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

        dbHandler = new DBHandler(UpdateLocationActivity.this);

        //Get data passed by adapter class
        locationName = getIntent().getStringExtra("name");
        locationType = getIntent().getStringExtra("type");
        latitude = getIntent().getStringExtra("latitude");
        longitude = getIntent().getStringExtra("longitude");
        altitude = getIntent().getStringExtra("altitude");
        logDate = getIntent().getStringExtra("date");

       //Update UI components
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

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);

        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        locationTypeSpinner.setAdapter(dataAdapter);
        locationTypeSpinner.setSelection(dataAdapter.getPosition(locationType));
        updateLocationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbHandler.updateLocation(locationName, locationNameEdt.getText().toString(), latitudeEdt.getText().toString(), longitudeEdt.getText().toString(), altitudeEdt.getText().toString(), locationTypeSpinner.getSelectedItem().toString(), logDateEdt.getText().toString());

                Toast.makeText(UpdateLocationActivity.this, "Device Updated..", Toast.LENGTH_SHORT).show();

                Intent i = new Intent(UpdateLocationActivity.this, MainActivity.class);
                startActivity(i);
            }
        });

        logDateEdt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker(UpdateLocationActivity.this);
            }
        });

        getLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateGPS();
            }
        });

        deleteLocationeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(UpdateLocationActivity.this);

                builder.setMessage("¿Quieres borrar la ubicación?");

                builder.setTitle("Borrar ubicación");

                builder.setCancelable(false);

                builder.setPositiveButton("Yes", (DialogInterface.OnClickListener) (dialog, which) -> {
                    dbHandler.deleteLocation(locationName);
                    Toast.makeText(UpdateLocationActivity.this, "Deleted the location", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(UpdateLocationActivity.this, MainActivity.class);
                    startActivity(i);
                });

                builder.setNegativeButton("No", (DialogInterface.OnClickListener) (dialog, which) -> {
                    dialog.cancel();
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
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
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(android.location.Location location) {
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

    //Display DatePickerDialog
    private void showDatePicker(Context context) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        DatePickerDialog datePickerDialog = new DatePickerDialog(context,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        logDateEdt.setText(String.valueOf(year));
                    }
                }, year, calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        datePickerDialog.show();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.actionbar, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_share) {
            shareLocationData();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //Share device data
    private void shareLocationData() {
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
