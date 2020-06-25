package com.example.englishlearningapp.utils;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import java.util.HashSet;

public class GlobalVariable extends Application {
    private static GlobalVariable instance = null;
    private GlobalVariable(Context context){
        instance = (GlobalVariable) context.getApplicationContext();
    };

    public GlobalVariable(){}

    public static GlobalVariable getInstance(Context context) {
        if(instance == null){
            instance = new GlobalVariable(context);
        }
        return instance;
    }
    private HashSet<Integer> randomNumbers = new HashSet<>();

    public boolean addToHashSet(int num){
        return randomNumbers.add(num);
    }
    public int getHashSetSize(){
        return randomNumbers.size();
    }

    public HashSet getHashSet(){
        return randomNumbers;
    }

    public void clearHashSet(){
        randomNumbers.clear();
    }

    public static void changeStatusBarColor(Activity activity){
        Window window = activity.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.parseColor("#03A9F4"));

    }


}
