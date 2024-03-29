package com.example.entrega.controller;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.entrega.view.LocationModal;

import java.util.ArrayList;

public class DBHandler extends SQLiteOpenHelper {

    //Constant variables for DB
    private static final String DB_NAME = "Devicedb";
    private static final int DB_VERSION = 1;
    private static final String TABLE_NAME = "myDevices";
    //Column names
    private static final String ID_COL = "id";
    private static final String NAME_COL = "name";
    private static final String LOG_DATE = "date";
    private static final String LATITUDE = "latitude";
    private static final String LONGITUDE = "longitude";
    private static final String ALTITUDE = "altitude";
    private static final String LANDMARK_TYPE = "type";

    // DB Constructor
    public DBHandler(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    //DB Create Query
    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + TABLE_NAME + " ("
                + ID_COL + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + NAME_COL + " TEXT,"
                + LOG_DATE + " TEXT,"
                + LATITUDE + " TEXT,"
                + LONGITUDE + " TEXT,"
                + ALTITUDE + " TEXT,"
                + LANDMARK_TYPE + " TEXT)";

        db.execSQL(query);
    }

    //Insert
    public void addNewLocation(String DeviceName, String logDate, String longitude, String latitude, String altitude, String landmarkType) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(NAME_COL, DeviceName);
        values.put(LOG_DATE, logDate);
        values.put(LATITUDE, latitude);
        values.put(LONGITUDE, longitude);
        values.put(ALTITUDE, altitude);
        values.put(LANDMARK_TYPE, landmarkType);

        db.insert(TABLE_NAME, null, values);

        db.close();
    }

    //Select ALL
    public ArrayList<LocationModal> readLocations() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursorDevices = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);

        ArrayList<LocationModal> ModalArrayList = new ArrayList<>();

        if (cursorDevices.moveToFirst()) {
            do {
                ModalArrayList.add(new LocationModal(cursorDevices.getString(1),
                        cursorDevices.getString(2),
                        cursorDevices.getString(3),
                        cursorDevices.getString(4),
                        cursorDevices.getString(5),
                        cursorDevices.getString(6)
                ));
            } while (cursorDevices.moveToNext());
        }
        cursorDevices.close();
        return ModalArrayList;
    }

    //Update Location
    public void updateLocation(String originalLocationName, String LocationName, String Latitude, String Longitude, String Altitude,
                               String LocationType, String logDate) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(NAME_COL, LocationName);
        values.put(LOG_DATE, logDate);
        values.put(LATITUDE, Latitude);
        values.put(LONGITUDE, Longitude);
        values.put(ALTITUDE, Altitude);
        values.put(LANDMARK_TYPE, LocationType);

        db.update(TABLE_NAME, values, "name=?", new String[]{originalLocationName});
        db.close();
    }

    // Delete Location
    public void deleteLocation(String DeviceName) {
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(TABLE_NAME, "name=?", new String[]{DeviceName});
        db.close();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
}
