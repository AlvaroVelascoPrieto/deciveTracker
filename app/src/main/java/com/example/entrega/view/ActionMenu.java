package com.example.entrega.view;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.entrega.R;
import com.example.entrega.controller.DBHandler;
import com.example.entrega.model.MyWorker;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.Iterator;

public class ActionMenu extends AppCompatActivity {

    public Button viewProfileData, addNewLocation, seeAllLocations, signOut;
    private String id, password, name, lastname, phone, longitude, latitude;
    FusedLocationProviderClient fusedLocationProviderClient;
    static final int PERMISSIONS_FINE_LOCATION = 99;
    static final int ACCESS_BACKGROUND_LOCATION = 98;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.action_menu);

        viewProfileData = findViewById(R.id.idBtnProfile);
        addNewLocation = findViewById(R.id.idBtnAddLocation);
        seeAllLocations = findViewById(R.id.idBtnViewLocations);
        signOut = findViewById(R.id.idSignOut);

        viewProfileData.setCompoundDrawablesWithIntrinsicBounds(null, getDrawable(R.drawable.baseline_account_circle_24), null, null);
        addNewLocation.setCompoundDrawablesWithIntrinsicBounds(null, getDrawable(R.drawable.baseline_add_location_alt_24), null, null);
        seeAllLocations.setCompoundDrawablesWithIntrinsicBounds(null, getDrawable(R.drawable.baseline_auto_awesome_motion_24), null, null);
        id = getIntent().getStringExtra("id");
        password = getIntent().getStringExtra("password");
        name = getIntent().getStringExtra("name");
        lastname = getIntent().getStringExtra("lastname");
        phone = getIntent().getStringExtra("phone");

        viewProfileData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ActionMenu.this, ProfileActivity.class);

                i.putExtra("id", id);
                i.putExtra("password", password);
                i.putExtra("name", name);
                i.putExtra("lastname", lastname);
                i.putExtra("phone", phone);
                startActivity(i);
            }
        });

        addNewLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ActionMenu.this, MainActivity.class);
                startActivity(i);
            }
        });

        seeAllLocations.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ActionMenu.this, ViewLocations.class);
                startActivity(i);
            }
        });

        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ActionMenu.this, LogIn.class);
                startActivity(i);
            }
        });

        updateGPS();


    }

    private String insideOrOutside(Location origin, float radius, Location current){
        float distance = origin.distanceTo(current);
        if (distance<=radius){
            return "inside";
        }
        return "outside";
    }

    private void updateGPS(){
        System.out.println("AAA");
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        if (this.checkCallingOrSelfPermission("android.permission.ACCESS_FINE_LOCATION") == PackageManager.PERMISSION_GRANTED){
            System.out.println("UUU");
            //User provided permissions
            //Get last location (not current) last stored one
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(android.location.Location location) {
                    latitude = String.valueOf(location.getLatitude());
                    longitude = String.valueOf(location.getLongitude());
                    System.out.println("EEEE");
                    Location mLocation = new Location("Current");
                    mLocation.setLongitude(Float.valueOf(longitude));
                    mLocation.setLatitude(Float.valueOf(latitude));
                    DBHandler dbHandler = new DBHandler(ActionMenu.this.getApplicationContext());
                    ArrayList<LocationModal> locations = dbHandler.readLocations();
                    Iterator<LocationModal> iter = locations.iterator();
                    while (iter.hasNext()){
                        LocationModal act = iter.next();
                        Location origin = new Location("Origin");
                        origin.setLatitude(Float.valueOf(act.getLatitude()));
                        origin.setLongitude(Float.valueOf(act.getLongitude()));
                        String position = insideOrOutside(origin, 100.0F, mLocation);
                        System.out.println(act.getId());
                        System.out.println(dbHandler.getLastEvent(String.valueOf(act.getId())));
                        System.out.println(position);
                        if (dbHandler.getLastEvent(String.valueOf(act.getId()))!=null && !dbHandler.getLastEvent(String.valueOf(act.getId())).equals(position)){
                            dbHandler.addNewEvent(String.valueOf(act.getId()), position);
                            System.out.println("Executed");
                        } else if (dbHandler.getLastEvent(String.valueOf(act.getId()))==null) {
                            dbHandler.addNewEvent(String.valueOf(act.getId()), position);
                            System.out.println("UE");
                        }
                    }
                }
            });
        }
        else{
            if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.M){
                requestPermissions (new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_FINE_LOCATION);
            }
        }
        if (this.checkCallingOrSelfPermission("android.permission.ACCESS_BACKGROUND_LOCATION") == PackageManager.PERMISSION_GRANTED){

        }
        else{
            if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.M){
                requestPermissions (new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION}, ACCESS_BACKGROUND_LOCATION);
            }
        }
    }
}
