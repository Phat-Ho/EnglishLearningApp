package com.example.englishlearningapp.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefsManager {
    SharedPreferences sharedPreferences;
    public SharedPreferences.Editor editor;
    public Context context;

    public static final String PREF_NAME = "SharedPrefsManager";
    public static final String SORT_BY = "SORT_BY";
    public static final String VIEW_TYPE = "VIEW_TYPE";
    public static final int EXPAND = 0;
    public static final int COLLAPSE = 1;
    public static final int BY_ALPHABET = 0;
    public static final int BY_TIME = 1;

    public SharedPrefsManager(Context context) {
        this.context = context;
        this.sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        this.editor = sharedPreferences.edit();
    }

    public void setSortBy(int sort){
        if(sort == EXPAND){
            editor.putInt(SORT_BY, EXPAND);
            editor.apply();
        }else{
            editor.putInt(SORT_BY, COLLAPSE);
            editor.apply();
        }
    }

    public int getSortBy(){
        return sharedPreferences.getInt(SORT_BY, 0);
    }

    public void setViewType(int viewType){
        if(viewType == BY_ALPHABET){
            editor.putInt(VIEW_TYPE, BY_ALPHABET);
            editor.apply();
        }else{
            editor.putInt(VIEW_TYPE, BY_TIME);
            editor.apply();
        }
    }

    public int getViewType(){
        return sharedPreferences.getInt(VIEW_TYPE, 0);
    }
}
