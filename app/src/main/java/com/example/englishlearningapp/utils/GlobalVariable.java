package com.example.englishlearningapp.utils;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.view.View;
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

    public static void hideStatusBar(Activity activity){
        if (Build.VERSION.SDK_INT < 16) {
            activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        else {
            View decorView = activity.getWindow().getDecorView();
            // Hide Status Bar.
            int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }


}
