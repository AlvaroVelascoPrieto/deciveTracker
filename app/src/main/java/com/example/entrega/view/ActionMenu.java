package com.example.entrega.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.entrega.R;

public class ActionMenu extends AppCompatActivity {

    public Button viewProfileData, addNewLocation, seeAllLocations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.action_menu);

        viewProfileData = findViewById(R.id.idBtnProfile);
        addNewLocation = findViewById(R.id.idBtnAddLocation);
        seeAllLocations = findViewById(R.id.idBtnViewLocations);

        viewProfileData.setCompoundDrawablesWithIntrinsicBounds(null, getDrawable(R.drawable.baseline_account_circle_24), null, null);
        addNewLocation.setCompoundDrawablesWithIntrinsicBounds(null, getDrawable(R.drawable.baseline_add_location_alt_24), null, null);
        seeAllLocations.setCompoundDrawablesWithIntrinsicBounds(null, getDrawable(R.drawable.baseline_auto_awesome_motion_24), null, null);

        viewProfileData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO
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

    }
}
