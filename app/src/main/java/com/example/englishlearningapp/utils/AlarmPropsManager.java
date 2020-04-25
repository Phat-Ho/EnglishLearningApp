package com.example.englishlearningapp.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class AlarmPropsManager {
    SharedPreferences sharedPreferences;
    public SharedPreferences.Editor editor;
    public Context context;

    public static final String PREF_NAME = "ALARM";
    public static final String COUNT = "ALARM_COUNT";
    public static final String INDEX = "INDEX";
    public static final String NO_WORD = "NO_WORD";
    public static final String START_HOUR = "START_HOUR";
    public static final String END_HOUR = "END_HOUR";
    public static final String ALARM_TYPE = "ALARM_TYPE";

    public AlarmPropsManager(Context context) {
        this.context = context;
        this.sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        this.editor = sharedPreferences.edit();
    }

    public int getAlarmCount(){
        return sharedPreferences.getInt(COUNT, 0);
    }

    public void setAlarmCount(int count){
        editor.putInt(COUNT, count);
        editor.apply();
    }

    public int getIndex(){
        return sharedPreferences.getInt(INDEX, 0);
    }

    public void setIndex(int index){
        editor.putInt(INDEX, index);
        editor.apply();
    }

    public int getWordNo(){
        return sharedPreferences.getInt(NO_WORD, 1);
    }

    public void setWordNo(int wordNo){
        editor.putInt(NO_WORD, wordNo);
        editor.apply();
    }

    public int getStartHour(){
        return sharedPreferences.getInt(START_HOUR, 7);
    }

    public void setStartHour(int startHour){
        editor.putInt(START_HOUR, startHour);
        editor.apply();
    }

    public int getEndHour(){
        return sharedPreferences.getInt(END_HOUR, 22);
    }

    public void setEndHour(int endHour){
        editor.putInt(START_HOUR, endHour);
        editor.apply();
    }

    public int getAlarmType(){
        return sharedPreferences.getInt(ALARM_TYPE, 0);
    }

    public void setAlarmType(int alarmType){
        editor.putInt(ALARM_TYPE, alarmType);
        editor.apply();
    }
}
