package com.example.englishlearningapp.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

public class DatabaseOpenHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "dict_hh.db";
    private static final int DATABASE_VERSION = 1;
    private static final String ID = "id";
    private static final String DATE = "date";
    private static final String TABLE_NAME = "history";
    private static final String TAG = "DatabaseOpenHelper";


    public DatabaseOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /*String createHistoryTableQuery = "CREATE TABLE " + TABLE_NAME + " (" +
                                        ID + " integer primary key, " +
                                        DATE + " text)";*/

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "onCreate: " + TABLE_NAME);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

}
