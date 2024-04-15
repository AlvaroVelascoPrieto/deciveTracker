package com.example.entrega.view;

import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.entrega.R;
import com.example.entrega.controller.DBHandler;
import com.example.entrega.model.MyWorker;

import java.util.ArrayList;
import java.util.Iterator;

public class LocationStats extends AppCompatActivity {
    TextView tvLocationStatus;
    private ArrayList<ArrayList<String>> events;
    private String locationId;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.location_stats);
        tvLocationStatus = findViewById(R.id.idTVLocationStatus);
        locationId = getIntent().getStringExtra("id");
        DBHandler dbHandler = new DBHandler(LocationStats.this.getApplicationContext());
        events = dbHandler.readEvents(locationId);
        Iterator<ArrayList<String>> iter = events.iterator();
        tvLocationStatus.setText("ALGO");
        while (iter.hasNext()){
            ArrayList<String> act = iter.next();
            if (act.get(3).equals("inside")){
                tvLocationStatus.setText(act.get(2));
            }else{
                tvLocationStatus.setText("NADA");
            }
        }
    }
}
