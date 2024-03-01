package com.example.entrega;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.entrega.controller.DBHandler;

public class MainActivity extends AppCompatActivity {

    // creating variables for our edittext, button and dbhandler
    private EditText deviceNameEdt, deviceYearEdt, deviceModelEdt, deviceTypeEdt;
    private Button addDeviceBtn, seeDevicesBtn;
    private DBHandler dbHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // initializing all our variables.
        deviceNameEdt = findViewById(R.id.idEdtDeviceName);
        deviceYearEdt = findViewById(R.id.idEdtDeviceYear);
        deviceModelEdt = findViewById(R.id.idEdtDeviceModel);
        deviceTypeEdt = findViewById(R.id.idEdtDeviceType);
        addDeviceBtn = findViewById(R.id.idBtnAddDevice);
        seeDevicesBtn = findViewById(R.id.idBtnSeeDevices);

        // creating a new dbhandler class
        // and passing our context to it.
        dbHandler = new DBHandler(MainActivity.this);

        // below line is to add on click listener for our add course button.
        addDeviceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // below line is to get data from all edit text fields.
                String deviceName = deviceNameEdt.getText().toString();
                String deviceYear = deviceYearEdt.getText().toString();
                String deviceModel = deviceModelEdt.getText().toString();
                String deviceType = deviceTypeEdt.getText().toString();

                // validating if the text fields are empty or not.
                if (deviceName.isEmpty() || deviceYear.isEmpty() || deviceModel.isEmpty() || deviceType.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Please enter all the data..", Toast.LENGTH_SHORT).show();
                    return;
                }

                // on below line we are calling a method to add new
                // course to sqlite data and pass all our values to it.
                dbHandler.addNewDevice(deviceName, deviceYear, deviceModel, deviceType);

                // after adding the data we are displaying a toast message.
                Toast.makeText(MainActivity.this, "System has been added.", Toast.LENGTH_SHORT).show();
                deviceNameEdt.setText("");
                deviceModelEdt.setText("");
                deviceYearEdt.setText("");
                deviceTypeEdt.setText("");
            }
        });

        seeDevicesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // opening a new activity via a intent.
                Intent i = new Intent(MainActivity.this, ViewDevices.class);
                startActivity(i);
            }
        });
    }
}
