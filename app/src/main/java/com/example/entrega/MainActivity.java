package com.example.entrega;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.entrega.controller.DBHandler;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    // creating variables for our edittext, button and dbhandler
    private EditText deviceNameEdt, deviceYearEdt, deviceModelEdt;
    private Spinner deviceTypeSpinner;
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
        deviceTypeSpinner = findViewById(R.id.idSpinerDeviceType);
        addDeviceBtn = findViewById(R.id.idBtnAddDevice);
        seeDevicesBtn = findViewById(R.id.idBtnSeeDevices);

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
                String deviceType = deviceTypeSpinner.getSelectedItem().toString();

                // Notification Channel ID. You can create multiple channels if needed.
                String channelId = "my_channel_id";
                CharSequence channelName = "My Channel";

                NotificationManager notificationManager =
                        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                // Check if the channel exists (required for API 26 and above)
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    NotificationChannel channel = notificationManager.getNotificationChannel(channelId);
                    if (channel == null) {
                        // Create the channel if it doesn't exist
                        channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT);
                        // Configure the channel's behavior
                        channel.setDescription("My Channel Description");
                        // Register the channel with the system
                        notificationManager.createNotificationChannel(channel);
                    }
                }

                // Now, you can create and show notifications using this channel
                NotificationCompat.Builder builder = new NotificationCompat.Builder(MainActivity.this, channelId)
                        .setSmallIcon(R.drawable.ic_launcher_foreground)
                        .setContentTitle("Dispositivo añadido")
                        .setContentText(deviceName);

                // Show the notification
                notificationManager.notify(1, builder.build());

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
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("My channel", "MyNotificationChannel", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

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
