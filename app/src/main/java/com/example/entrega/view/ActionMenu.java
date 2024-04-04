package com.example.entrega.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.entrega.R;

public class ActionMenu extends AppCompatActivity {

    public Button viewProfileData, addNewLocation, seeAllLocations, signOut;
    private String id, password, name, lastname, phone;
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

    }
}
