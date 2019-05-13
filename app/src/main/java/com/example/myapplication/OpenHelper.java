package com.example.myapplication;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class OpenHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 4;
    private static final String DATABASE_NAME = "test1.db";
    private static final String TABLE_NAME = "test1db";
    private static final String _ID = "_id";
    private static final String time = "time";
    private static final String accelerometer = "accelerometer";
    private static final String latitude = "latitude";
    private static final String longitude = "longitude";
    //private static final String SQL_CREATE_ENTRIES = "CREATE TABLE "+ TABLE_NAME + " (" + _ID + " INTEGER PRIMARY KEY," + time + " time," +  accelerometer + " accelerometer,"+ latitude + " latitude," +  longitude + " longitude)";
    private static final String SQL_CREATE_ENTRIES = "CREATE TABLE "+ TABLE_NAME + " (" + _ID + " INTEGER PRIMARY KEY," + time + " time," +  accelerometer + " accelerometer)";
    private static final String SQL_DERETE_ENTRIES = "DROP TABLE IF EXISTS " + TABLE_NAME;

    OpenHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        Log.d("debug","super");
    }

    public void onCreate(SQLiteDatabase db){
        db.execSQL(SQL_CREATE_ENTRIES);//SQLiteファイルがなければSQLiteファイルが作成される
        Log.d("debug","onCreate(SQLiteDatabase)");
        Log.d("debug",SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        db.execSQL(SQL_DERETE_ENTRIES);
        onCreate(db);
        Log.d("debug","onUpgrade");
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion){
        onUpgrade(db, oldVersion, newVersion);
        Log.d("debug","onDowngrade");
    }
}
