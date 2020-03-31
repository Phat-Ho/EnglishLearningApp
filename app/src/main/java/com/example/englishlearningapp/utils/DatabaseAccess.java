package com.example.englishlearningapp.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.ArrayAdapter;

import com.example.englishlearningapp.activity.HistoryActivity;
import com.example.englishlearningapp.fragments.HistoryFragment;
import com.example.englishlearningapp.models.Word;

import java.util.ArrayList;

public class DatabaseAccess {

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
        Cursor cursor = database.rawQuery("SELECT * FROM av WHERE word LIKE '" + word + "%'" + "LIMIT 4000", null);
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
        String query = "SELECT av.id, av.word, av.html, av.description, av.pronounce, history.date " +
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

                wordList.add(word);
            }while (cursor.moveToNext());
        }
        return wordList;
    }

    public int addHistory(int id){
        ContentValues value = new ContentValues();
        value.put("id", id);
        database.insert("history", null, value);
        return id;
    }

    public int removeHistory(int id){
        return database.delete("history", "id = " + id, null);
    }
}
