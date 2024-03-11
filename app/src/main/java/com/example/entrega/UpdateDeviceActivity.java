package com.example.entrega;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.entrega.controller.DBHandler;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class UpdateDeviceActivity extends AppCompatActivity {

    // variables for our edit text, button, strings and dbhandler class.
    private EditText deviceNameEdt, deviceYearEdt, deviceModelEdt;
    private Spinner deviceTypeSpinner;
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
        deviceTypeSpinner = findViewById(R.id.idSpinerDeviceType);
        updateDeviceBtn = findViewById(R.id.idBtnUpdateDevice);
        deleteDeviceBtn = findViewById(R.id.idBtnDelete);

        // on below line we are initializing our dbhandler class.
        dbHandler = new DBHandler(UpdateDeviceActivity.this);

        // on below lines we are getting data which
        // we passed in our adapter class.
        deviceName = getIntent().getStringExtra("name");
        deviceType = getIntent().getStringExtra("year");
        deviceYear = getIntent().getStringExtra("model");
        deviceModel = getIntent().getStringExtra("type");
        System.out.println(deviceName);
        System.out.println(deviceYear);
        System.out.println(deviceModel);
        System.out.println(deviceType);
        // setting data to edit text
        // of our update activity.
        deviceNameEdt.setText(deviceName);
        deviceYearEdt.setText(deviceYear);
        deviceModelEdt.setText(deviceModel);

        // Spinner Drop down elements
        List<String> categories = new ArrayList<String>();
        categories.add("Phone");
        categories.add("Laptop");
        categories.add("Tablet");
        categories.add("Workstation");
        categories.add("Home PC");

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        deviceTypeSpinner.setAdapter(dataAdapter);
        deviceTypeSpinner.setSelection(dataAdapter.getPosition(deviceType));

        // adding on click listener to our update course button.
        updateDeviceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // inside this method we are calling an update course
                // method and passing all our edit text values.
                dbHandler.updateDevice(deviceName, deviceNameEdt.getText().toString(), deviceYearEdt.getText().toString(), deviceModelEdt.getText().toString(), deviceTypeSpinner.getSelectedItem().toString());

                // displaying a toast message that our course has been updated.
                Toast.makeText(UpdateDeviceActivity.this, "Course Updated..", Toast.LENGTH_SHORT).show();

                // launching our main activity.
                Intent i = new Intent(UpdateDeviceActivity.this, MainActivity.class);
                startActivity(i);
            }
        });

        deviceYearEdt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker(UpdateDeviceActivity.this);
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
                builder.setTitle("Delete device");

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
                        deviceYearEdt.setText(String.valueOf(year));
                    }
                }, year, calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        // Set the maximum date to the current date
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());

        // Show the DatePickerDialog
        datePickerDialog.show();
    }
}

