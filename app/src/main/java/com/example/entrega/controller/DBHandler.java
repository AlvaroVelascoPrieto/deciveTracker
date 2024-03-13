package com.example.entrega.controller;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.entrega.DeciveModal;

import java.util.ArrayList;

public class DBHandler extends SQLiteOpenHelper {

    // creating a constant variables for our database.
    // below variable is for our database name.
    private static final String DB_NAME = "Devicedb";

    // below int is our database version
    private static final int DB_VERSION = 1;

    // below variable is for our table name.
    private static final String TABLE_NAME = "myDevices";

    // below variable is for our id column.
    private static final String ID_COL = "id";

    // below variable is for our Device name column
    private static final String NAME_COL = "name";

    // below variable id for our Device duration column.
    private static final String LOG_DATE = "date";

    // below variable for our Device description column.
    private static final String LATITUDE = "latitude";

    private static final String LONGITUDE = "longitude";

    private static final String ALTITUDE = "altitude";

    // below variable is for our Device tracks column.
    private static final String LANDMARK_TYPE = "type";

    // creating a constructor for our database handler.
    public DBHandler(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    // below method is for creating a database by running a sqlite query
    @Override
    public void onCreate(SQLiteDatabase db) {
        // on below line we are creating
        // an sqlite query and we are
        // setting our column names
        // along with their data types.
        String query = "CREATE TABLE " + TABLE_NAME + " ("
                + ID_COL + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + NAME_COL + " TEXT,"
                + LOG_DATE + " TEXT,"
                + LATITUDE + " TEXT,"
                + LONGITUDE + " TEXT,"
                + ALTITUDE + " TEXT,"
                + LANDMARK_TYPE + " TEXT)";

        // at last we are calling a exec sql
        // method to execute above sql query
        db.execSQL(query);
    }

    // this method is use to add new Device to our sqlite database.
    public void addNewLocation(String DeviceName, String logDate, String longitude, String latitude, String altitude, String landmarkType) {

        // on below line we are creating a variable for
        // our sqlite database and calling writable method
        // as we are writing data in our database.
        SQLiteDatabase db = this.getWritableDatabase();

        // on below line we are creating a
        // variable for content values.
        ContentValues values = new ContentValues();

        // on below line we are passing all values
        // along with its key and value pair.
        values.put(NAME_COL, DeviceName);
        values.put(LOG_DATE, logDate);
        values.put(LATITUDE, latitude);
        values.put(LONGITUDE, longitude);
        values.put(ALTITUDE, altitude);
        values.put(LANDMARK_TYPE, landmarkType);

        // after adding all values we are passing
        // content values to our table.
        db.insert(TABLE_NAME, null, values);

        // at last we are closing our
        // database after adding database.
        db.close();
    }

    // we have created a new method for reading all the Devices.
    public ArrayList<DeciveModal> readDevices() {
        // on below line we are creating a
        // database for reading our database.
        SQLiteDatabase db = this.getReadableDatabase();

        // on below line we are creating a cursor with query to read data from database.
        Cursor cursorDevices = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);

        // on below line we are creating a new array list.
        ArrayList<DeciveModal> DeviceModalArrayList = new ArrayList<>();

        // moving our cursor to first position.
        if (cursorDevices.moveToFirst()) {
            do {
                // on below line we are adding the data from cursor to our array list.
                DeviceModalArrayList.add(new DeciveModal(cursorDevices.getString(1),
                        cursorDevices.getString(2),
                        cursorDevices.getString(3),
                        cursorDevices.getString(4),
                        cursorDevices.getString(5),
                        cursorDevices.getString(6)
                ));
            } while (cursorDevices.moveToNext());
            // moving our cursor to next.
        }
        // at last closing our cursor
        // and returning our array list.
        cursorDevices.close();
        return DeviceModalArrayList;
    }

    // below is the method for updating our Devices
    public void updateDevice(String originalLocationName, String LocationName, String Latitude, String Longitude, String Altitude,
                             String LocationType, String logDate) {

        // calling a method to get writable database.
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        // on below line we are passing all values
        // along with its key and value pair.
        values.put(NAME_COL, LocationName);
        values.put(LOG_DATE, logDate);
        values.put(LATITUDE, Latitude);
        values.put(LONGITUDE, Longitude);
        values.put(ALTITUDE, Altitude);
        values.put(LANDMARK_TYPE, LocationType);

        // on below line we are calling a update method to update our database and passing our values.
        // and we are comparing it with name of our Device which is stored in original name variable.
        db.update(TABLE_NAME, values, "name=?", new String[]{originalLocationName});
        db.close();
    }

    // below is the method for deleting our Device.
    public void deleteDevice(String DeviceName) {

        // on below line we are creating
        // a variable to write our database.
        SQLiteDatabase db = this.getWritableDatabase();

        // on below line we are calling a method to delete our
        // Device and we are comparing it with our Device name.
        db.delete(TABLE_NAME, "name=?", new String[]{DeviceName});
        db.close();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // this method is called to check if the table exists already.
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
}
