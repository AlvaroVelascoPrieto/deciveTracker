package com.example.entrega.model;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import com.example.entrega.controller.DBHandler;

import com.example.entrega.R;
import com.example.entrega.view.LocationModal;
import com.example.entrega.view.ViewLocations;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import com.example.entrega.R;
import com.example.entrega.controller.DBHandler;
import com.example.entrega.model.MyWorker;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

public class MyWorker extends Worker {
    private static final String TAG = "MyWorker";

    /**
     * The desired interval for location updates. Inexact. Updates may be more or less frequent.
     */
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;

    /**
     * The fastest rate for active location updates. Updates will never be more frequent
     * than this value.
     */
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;
    /**
     * The current location.
     */
    private Location mLocation;

    /**
     * Provides access to the Fused Location Provider API.
     */
    private FusedLocationProviderClient mFusedLocationClient;

    private Context mContext;
    /**
     * Callback for changes in location.
     */
    private LocationCallback mLocationCallback;
    FusedLocationProviderClient fusedLocationProviderClient;

    public MyWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        mContext = context;

    }

    @NonNull
    @Override
    public Result doWork() {
        Log.d(TAG, "doWork: Done");
        Log.d(TAG, "onStartJob: STARTING JOB..");

        DateFormat dateFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());

        Calendar c = Calendar.getInstance();
        Date date = c.getTime();
        String formattedDate = dateFormat.format(date);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(mContext);
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
            }
        };

        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        try {
            Date currentDate = dateFormat.parse(formattedDate);
            mFusedLocationClient
                    .getLastLocation()
                    .addOnCompleteListener(new OnCompleteListener<Location>() {
                        @Override
                        public void onComplete(@NonNull Task<Location> task) {
                            if (task.isSuccessful() && task.getResult() != null) {
                                mLocation = task.getResult();
                                Log.d(TAG, "Location : " + mLocation);
                                DBHandler dbHandler = new DBHandler(MyWorker.this.getApplicationContext());
                                ArrayList<LocationModal> locations = dbHandler.readLocations();
                                Iterator<LocationModal> iter = locations.iterator();
                                while (iter.hasNext()){
                                    LocationModal act = iter.next();
                                    Location origin = new Location("Origin");
                                    origin.setLatitude(Float.valueOf(act.getLatitude()));
                                    origin.setLongitude(Float.valueOf(act.getLongitude()));
                                    String position = insideOrOutside(origin, 100.0F, mLocation);
                                    if (dbHandler.getLastEvent(String.valueOf(act.getId()))!=null && !dbHandler.getLastEvent(String.valueOf(act.getId())).equals(position)){
                                        dbHandler.addNewEvent(String.valueOf(act.getId()), position);
                                    }
                                    System.out.println("Exectuted");
                                }
                                // Create the NotificationChannel, but only on API 26+ because
                                // the NotificationChannel class is new and not in the support library
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                    CharSequence name = mContext.getString(R.string.app_name);
                                    String description = mContext.getString(R.string.app_name);
                                    int importance = NotificationManager.IMPORTANCE_DEFAULT;
                                    NotificationChannel channel = new NotificationChannel(mContext.getString(R.string.app_name), name, importance);
                                    channel.setDescription(description);
                                    // Register the channel with the system; you can't change the importance
                                    // or other notification behaviors after this
                                    NotificationManager notificationManager = mContext.getSystemService(NotificationManager.class);
                                    notificationManager.createNotificationChannel(channel);
                                }

                                NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext, mContext.getString(R.string.app_name))
                                        .setSmallIcon(android.R.drawable.ic_menu_mylocation)
                                        .setContentTitle("New Location Update")
                                        .setContentText("You are at Lat: " + mLocation.getLatitude() + " Long: " + mLocation.getLongitude())
                                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                                        .setStyle(new NotificationCompat.BigTextStyle().bigText("You are at Lat: " + mLocation.getLatitude() + " Long: " + mLocation.getLongitude()));

                                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(mContext);

                                // notificationId is a unique int for each notification that you must define
                                notificationManager.notify(1001, builder.build());

                                mFusedLocationClient.removeLocationUpdates(mLocationCallback);
                            } else {
                                Log.w(TAG, "Failed to get location.");
                            }
                        }
                    });
        } catch (SecurityException unlikely) {
            Log.e(TAG, "Lost location permission." + unlikely);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        try {
            mFusedLocationClient.requestLocationUpdates(mLocationRequest, null);
        } catch (SecurityException unlikely) {
            //Utils.setRequestingLocationUpdates(this, false);
            Log.e(TAG, "Lost location permission. Could not request updates. " + unlikely);
        }

        return Result.success();
    }

    private String insideOrOutside(Location origin, float radius, Location current){
        float distance = origin.distanceTo(current);
        if (distance<=radius){
            return "inside";
        }
        return "outside";
    }
}