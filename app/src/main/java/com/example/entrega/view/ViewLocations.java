package com.example.entrega.view;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.entrega.R;
import com.example.entrega.controller.DBHandler;

import java.util.ArrayList;

public class ViewLocations extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_device);

        //Array to populate it with locations
        ArrayList<LocationModal> courseModalArrayList = new ArrayList<>();

        DBHandler dbHandler = new DBHandler(ViewLocations.this);

        //Getting all locations on the DB
        courseModalArrayList = dbHandler.readLocations();

        //Getting recycler view using
        LocationRVAdapter locationRVAdapter = new LocationRVAdapter(courseModalArrayList, ViewLocations.this);
        RecyclerView devicesRV = findViewById(R.id.idRVDevices);

        // setting layout manager for our recycler view.
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ViewLocations.this, RecyclerView.VERTICAL, false);
        devicesRV.setLayoutManager(linearLayoutManager);

        // setting our adapter to recycler view.
        devicesRV.setAdapter(locationRVAdapter);
    }
}

