package com.example.englishlearningapp.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

public class DatabaseOpenHelper extends SQLiteAssetHelper {
    private static final String DATABASE_NAME = "dict_hh.db";
    private static final int DATABASE_VERSION = 6;
    private static final String TAG = "DatabaseOpenHelper";
    public final Context context;

    public DatabaseOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        setForcedUpgrade();
        this.context = context;
        long dbSize = context.getDatabasePath(DATABASE_NAME).length();
        Log.d(TAG, "Database Size: " + context.getDatabasePath(DATABASE_NAME).length());
        if(dbSize < 30000){
            context.deleteDatabase(DATABASE_NAME);
            getReadableDatabase();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "upgrade: " + oldVersion + ", new: " + newVersion);
    }

}
