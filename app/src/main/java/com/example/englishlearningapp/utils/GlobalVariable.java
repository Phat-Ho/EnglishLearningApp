package com.example.englishlearningapp.utils;

import android.app.Application;
import android.content.Context;

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


}
