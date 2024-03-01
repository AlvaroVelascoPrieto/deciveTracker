package com.example.entrega;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.entrega.controller.DBHandler;

import java.util.ArrayList;

public class ViewDevices extends AppCompatActivity {

    // creating variables for our array list,
    // dbhandler, adapter and recycler view.
    private ArrayList<DeciveModal> courseModalArrayList;
    private DBHandler dbHandler;
    private DeviceRVAdapter deviceRVAdapter;
    private RecyclerView devicesRV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_device);

        // initializing our all variables.
        courseModalArrayList = new ArrayList<>();
        dbHandler = new DBHandler(ViewDevices.this);

        // getting our course array
        // list from db handler class.
        courseModalArrayList = dbHandler.readDevices();

        // on below line passing our array list to our adapter class.
        deviceRVAdapter = new DeviceRVAdapter(courseModalArrayList, ViewDevices.this);
        devicesRV = findViewById(R.id.idRVDevices);

        // setting layout manager for our recycler view.
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ViewDevices.this, RecyclerView.VERTICAL, false);
        devicesRV.setLayoutManager(linearLayoutManager);

        // setting our adapter to recycler view.
        devicesRV.setAdapter(deviceRVAdapter);
    }
}

