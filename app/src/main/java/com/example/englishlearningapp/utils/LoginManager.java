package com.example.englishlearningapp.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class LoginManager {
    SharedPreferences sharedPreferences;
    public SharedPreferences.Editor editor;
    public Context context;

    public static final String PREF_NAME = "LOGIN";
    public static final String NAME = "NAME";
    public static final String EMAIL = "EMAIL";
    public static final String ID = "USER_ID";

    public LoginManager(Context context) {
        this.context = context;
        this.sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void createUserData(String name, String email, int id){
        editor = sharedPreferences.edit();
        editor.putInt(ID, id);
        editor.putString(NAME, name);
        editor.putString(EMAIL, email);
        editor.apply();
    }

    public boolean isLogin(){
        if(sharedPreferences.getInt(ID, 0) > 0){
            return true;
        }
        return false;
    }

    public String getUserName(){
        return sharedPreferences.getString(NAME, null);
    }

    public String getUserEmail(){
        return sharedPreferences.getString(EMAIL, null);
    }

    public int getUserId(){
        return sharedPreferences.getInt(ID, 0);
    }

    public void logout(){
        editor = sharedPreferences.edit();
        editor.clear();
        editor.commit();
    }
}
