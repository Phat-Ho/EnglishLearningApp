package com.example.englishlearningapp.utils;

import java.net.PortUnreachableException;

public class DatabaseContract {
    public static String HISTORY_TABLE = "history";
    public static String FAVORITE_TABLE = "favorite";
    public static String HISTORY_DATE_TABLE = "historyDate";
    public static String REMINDED_TABLE = "remindWord";
    public static String REMINDED_DATE_TABLE = "remindWordDate";
    public static String TOPIC_REMEMBER_TABLE = "topicRemember";
    public static String REMEMBERED_TABLE = "remembered";
    public static String WORD_ID = "wordId";
    public static String TOPIC_ID = "topicId";
    public static String SYNC_STATUS = "sync_status";
    public static String REMEMBERED = "remembered";
    public static int SYNC = 1;
    public static int NOT_SYNC = 0;
    public static String DATE = "date";
    public static int ALARM_HISTORY = 99;
    public static int ALARM_FAVORITE = 100;
}
