package com.example.entrega;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.entrega.controller.DBHandler;

public class UpdateDeviceActivity extends AppCompatActivity {

    // variables for our edit text, button, strings and dbhandler class.
    private EditText deviceNameEdt, deviceYearEdt, deviceModelEdt, deviceTypeEdt;
    private Button updateDeviceBtn, deleteDeviceBtn;
    private DBHandler dbHandler;
    String deviceName, deviceYear, deviceModel, deviceType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_device);

        // initializing all our variables.
        deviceNameEdt = findViewById(R.id.idEdtDeviceName);
        deviceYearEdt = findViewById(R.id.idEdtDeviceYear);
        deviceModelEdt = findViewById(R.id.idEdtDeviceModel);
        deviceTypeEdt = findViewById(R.id.idEdtDeviceType);
        updateDeviceBtn = findViewById(R.id.idBtnUpdateDevice);
        deleteDeviceBtn = findViewById(R.id.idBtnDelete);

        // on below line we are initializing our dbhandler class.
        dbHandler = new DBHandler(UpdateDeviceActivity.this);

        // on below lines we are getting data which
        // we passed in our adapter class.
        deviceName = getIntent().getStringExtra("name");
        deviceYear = getIntent().getStringExtra("year");
        deviceModel = getIntent().getStringExtra("model");
        deviceType = getIntent().getStringExtra("type");

        // setting data to edit text
        // of our update activity.
        deviceNameEdt.setText(deviceName);
        deviceYearEdt.setText(deviceYear);
        deviceModelEdt.setText(deviceModel);
        deviceTypeEdt.setText(deviceType);

        // adding on click listener to our update course button.
        updateDeviceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // inside this method we are calling an update course
                // method and passing all our edit text values.
                dbHandler.updateDevice(deviceName, deviceNameEdt.getText().toString(), deviceYearEdt.getText().toString(), deviceModelEdt.getText().toString(), deviceTypeEdt.getText().toString());

                // displaying a toast message that our course has been updated.
                Toast.makeText(UpdateDeviceActivity.this, "Course Updated..", Toast.LENGTH_SHORT).show();

                // launching our main activity.
                Intent i = new Intent(UpdateDeviceActivity.this, MainActivity.class);
                startActivity(i);
            }
        });

        // adding on click listener for delete button to delete our course.
        deleteDeviceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create the object of AlertDialog Builder class
                AlertDialog.Builder builder = new AlertDialog.Builder(UpdateDeviceActivity.this);

                // Set the message show for the Alert time
                builder.setMessage("Do you really want to delete this device?");

                // Set Alert Title
                builder.setTitle("DELETE DEVICE");

                // Set Cancelable false for when the user clicks on the outside the Dialog Box then it will remain show
                builder.setCancelable(false);

                // Set the positive button with yes name Lambda OnClickListener method is use of DialogInterface interface.
                builder.setPositiveButton("Yes", (DialogInterface.OnClickListener) (dialog, which) -> {
                    // When the user click yes button then app will close
                    dbHandler.deleteDevice(deviceName);
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
                // calling a method to delete our course.

            }
        });
    }
}
