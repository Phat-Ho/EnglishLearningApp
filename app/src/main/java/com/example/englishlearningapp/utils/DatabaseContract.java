package com.example.englishlearningapp.utils;

import java.net.PortUnreachableException;

public class DatabaseContract {
    public static String HISTORY_TABLE = "history";
    public static String FAVORITE_TABLE = "favorite";
    public static String HISTORY_DATE_TABLE = "historyDate";
    public static String REMINDED_TABLE = "remindWord";
    public static String REMINDED_DATE_TABLE = "remindWordDate";
    public static String WORD_ID = "wordId";
    public static String SYNC_STATUS = "sync_status";
    public static String REMEMBERED = "remembered";
    public static int SYNC = 1;
    public static int NOT_SYNC = 0;
    public static String DATE = "date";
    public static int ALARM_HISTORY = 0;
    public static int ALARM_FAVORITE = 1;
}
