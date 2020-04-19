package com.example.englishlearningapp.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.ArrayAdapter;

import com.example.englishlearningapp.activity.HistoryActivity;
import com.example.englishlearningapp.fragments.HistoryFragment;
import com.example.englishlearningapp.models.Word;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class DatabaseAccess {

    private static final String TAG = "Database";
    private SQLiteOpenHelper openHelper;
    private SQLiteDatabase database;
    private static DatabaseAccess instance;

    /**
     * Private constructor to avoid object creation from outside classes.
     *
     * @param context
     */
    private DatabaseAccess(Context context) {
        this.openHelper = new DatabaseOpenHelper(context);
    }

    /**
     * Return a singleton instance of DatabaseAccess.
     *
     * @param context the Context
     * @return the instance of DatabaseAccess
     */
    public static DatabaseAccess getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseAccess(context);
        }
        return instance;
    }

    /**
     * Open the database connection.
     */
    public void open() {
        this.database = openHelper.getWritableDatabase();
    }

    /**
     * Close the database connection.
     */
    public void close() {
        if (database != null) {
            this.database.close();
        }
    }

    public SQLiteDatabase getDatabase() {
        return database;
    }

    /**
     * Read all quotes from the database.
     *
     * @return a List of quotes
     */
    public ArrayList<Word> getWords(String word) {
        ArrayList<Word> list = new ArrayList<>();
        Cursor cursor = database.rawQuery("SELECT * FROM av WHERE word LIKE '" + word + "%'" + "LIMIT 100", null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            list.add(new Word(cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getString(cursor.getColumnIndex("description")),
                    cursor.getString(cursor.getColumnIndex("pronounce")),
                    cursor.getString(cursor.getColumnIndex("html"))));
            cursor.moveToNext();
        }
        cursor.close();
        return list;
    }

    public ArrayList<Word> getHistoryWords(){
        ArrayList<Word> wordList = new ArrayList<>();
        String query = "SELECT av.id, av.word, av.html, av.description, av.pronounce, history.date, history.remembered " +
                         "FROM history JOIN av ON history.id = av.id";
        Cursor cursor = database.rawQuery(query, null);
        if(cursor.moveToFirst()){
            do{
                Word word = new Word();
                word.setId(cursor.getInt(0));
                word.setWord(cursor.getString(1));
                word.setHtml(cursor.getString(2));
                word.setDescription(cursor.getString(3));
                word.setPronounce(cursor.getString(4));
                word.setRemembered(cursor.getInt(6));

                wordList.add(word);
            }while (cursor.moveToNext());
        }
        return wordList;
    }

    public int addHistory(int pWordID, int pSyncStatus, String pDate){
        int remembered = 0;
        ContentValues value = new ContentValues();
        value.put(DatabaseContract.WORD_ID, pWordID);
        value.put(DatabaseContract.SYNC_STATUS, pSyncStatus);
        value.put(DatabaseContract.DATE, pDate);
        value.put(DatabaseContract.REMEMBERED, remembered);
        database.insert("history", null, value);
        return pWordID;
    }

    public Cursor readHistory(){
        String[] column = {DatabaseContract.WORD_ID, DatabaseContract.SYNC_STATUS, DatabaseContract.DATE};
        Cursor cursor = database.query(DatabaseContract.HISTORY_TABLE, column, null, null, null, null, null);

        return cursor;
    }

    public void updateHistorySyncStatus(int pWordID, int pSyncStatus){
        ContentValues values = new ContentValues();
        values.put(DatabaseContract.SYNC_STATUS, pSyncStatus);
        String selection = DatabaseContract.WORD_ID + " = " + pWordID;
        database.update(DatabaseContract.HISTORY_TABLE, values, selection, null);
    }

    public void updateHistoryRemembered(int pWordID, int remembered){
        ContentValues values = new ContentValues();
        values.put(DatabaseContract.REMEMBERED, remembered);
        String selection = DatabaseContract.WORD_ID + " = " + pWordID;
        database.update(DatabaseContract.HISTORY_TABLE, values, selection, null);
    }

    public int removeHistory(int id){
        return database.delete("history", "id = " + id, null);
    }

    public String getDatetime(){
        java.text.SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }

    public int addFavorite(int pWordID, int pSyncStatus){
        ContentValues value = new ContentValues();
        value.put(DatabaseContract.WORD_ID, pWordID);
        value.put(DatabaseContract.SYNC_STATUS, pSyncStatus);
        database.insert("favorite", null, value);
        return pWordID;
    }

    public ArrayList<Word> getFavoriteWords(){
        ArrayList<Word> wordList = new ArrayList<>();
        String query = "SELECT av.id, av.word, av.html, av.description, av.pronounce " +
                "FROM favorite JOIN av ON favorite.id = av.id";
        Cursor cursor = database.rawQuery(query, null);
        if(cursor.moveToFirst()){
            do{
                Word word = new Word();
                word.setId(cursor.getInt(0));
                word.setWord(cursor.getString(1));
                word.setHtml(cursor.getString(2));
                word.setDescription(cursor.getString(3));
                word.setPronounce(cursor.getString(4));

                wordList.add(word);
            }while (cursor.moveToNext());
        }
        return wordList;
    }

    public int removeFavorite(int id){
        return database.delete("favorite", "id = " + id, null);
    }
}
