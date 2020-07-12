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
    public static final String NUMBER = "NUMBER";
    public static final String ID = "USER_ID";
    public static  final String PASSWORD = "PASSWORD";
    private static final String DOB = "DOB";

    public LoginManager(Context context) {
        this.context = context;
        this.sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        this.editor = sharedPreferences.edit();
    }

    public void createUserData(int id, String name, String email, String password, String number, String dob){
        editor.putInt(ID, id);
        editor.putString(EMAIL, email);
        editor.putString(DOB, dob);
        if(name.equals("null")){
            editor.putString(NAME, "");
        }else{
            editor.putString(NAME, name);
        }

        if(number.equals("null")){
            editor.putString(NUMBER, "");
        }else{
            editor.putString(NUMBER, number);
        }

        editor.putString(PASSWORD, password);
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

    public String getUserPhoneNo(){
        return  sharedPreferences.getString(NUMBER, null);
    }

    public String getUserPassword(){
        return sharedPreferences.getString(PASSWORD, null);
    }

    public String getUserDob(){
        return sharedPreferences.getString(DOB, null);
    }

    public void setUserName(String userName){
        editor.putString(NAME, userName);
        editor.apply();
    }

    public void setUserPhoneNo(String phoneNo){
        editor.putString(NUMBER, phoneNo);
        editor.apply();
    }

    public void setUserDob(String dob){
        editor.putString(DOB, dob);
        editor.apply();
    }

    public void logout(){
        editor.clear();
        editor.apply();
    }
}
