package com.example.englishlearningapp.utils;

import android.app.Application;

import java.util.HashSet;

public class GlobalVariable extends Application {
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
