package com.example.englishlearningapp.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.ArrayAdapter;

import com.example.englishlearningapp.activity.HistoryActivity;
import com.example.englishlearningapp.fragments.HistoryFragment;
import com.example.englishlearningapp.models.Choice;
import com.example.englishlearningapp.models.MyDate;
import com.example.englishlearningapp.models.Question;
import com.example.englishlearningapp.models.Topic;
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
        open();
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

    public Word getWordsById(int wordId){
        Word word = new Word();
        Cursor cursor = database.rawQuery("SELECT * FROM av WHERE id = " + wordId, null);
        if(cursor.moveToFirst()){
            word.setId(cursor.getInt(0));
            word.setWord(cursor.getString(1));
            word.setDescription(cursor.getString(cursor.getColumnIndex("description")));
            word.setPronounce(cursor.getString(cursor.getColumnIndex("pronounce")));
            word.setHtml(cursor.getString(cursor.getColumnIndex("html")));
        }
        cursor.close();
        return word;
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
        cursor.close();
        return wordList;
    }

    public ArrayList<Word> getHistoryWordsToAlarm(){
        ArrayList<Word> wordList = new ArrayList<>();
        String query = "SELECT av.id, av.word, av.html, av.description, av.pronounce, history.date, history.remembered " +
                "FROM history JOIN av ON history.id = av.id WHERE history.remembered = 0";
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
        cursor.close();
        return wordList;
    }

    public ArrayList<Word> getVietnameseWords(String word){
        ArrayList<Word> list = new ArrayList<>();
        Cursor cursor = database.rawQuery("SELECT * FROM va WHERE word LIKE '" + word + "%'" + "LIMIT 100", null);
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

    public Word getHistoryWordById(int wordId){
        Word word = new Word();
        String query = "SELECT av.id, av.word, av.html, av.description, av.pronounce, history.date, history.remembered " +
                "FROM history JOIN av ON history.id = av.id WHERE av.id = " + wordId;
        Cursor cursor = database.rawQuery(query, null);
        if(cursor.moveToFirst()){
            word.setId(cursor.getInt(0));
            word.setWord(cursor.getString(1));
            word.setHtml(cursor.getString(2));
            word.setDescription(cursor.getString(3));
            word.setPronounce(cursor.getString(4));
            word.setRemembered(cursor.getInt(6));
        }
        cursor.close();
        return word;
    }

    public void addHistoryDate(int wordId, long timeInMillis){
        ContentValues values = new ContentValues();
        values.put(DatabaseContract.WORD_ID, wordId);
        values.put(DatabaseContract.DATE, timeInMillis);
        database.insert(DatabaseContract.HISTORY_DATE_TABLE, null, values);
    }

    public int addHistory(int pWordID, int pSyncStatus, long pDate){
        int remembered = 0;
        ContentValues value = new ContentValues();
        value.put("id", pWordID);
        value.put(DatabaseContract.SYNC_STATUS, pSyncStatus);
        value.put(DatabaseContract.DATE, pDate);
        value.put(DatabaseContract.REMEMBERED, remembered);
        database.insert("history", null, value);
        return pWordID;
    }

    public ArrayList<MyDate> getHistoryDateByWordId(int wordId){
        ArrayList<MyDate> dateList = new ArrayList<>();
        String query = "SELECT history.id, historyDate.date FROM history " +
                "JOIN historyDate ON history.id = historyDate.wordId WHERE history.id = " + wordId;
        Cursor cursor = database.rawQuery(query, null);
        if(cursor.moveToFirst()){
            do{
                MyDate date = new MyDate(cursor.getLong(1));
                dateList.add(date);
            }while (cursor.moveToNext());
        }
        cursor.close();
        return dateList;
    }

    public void setHistoryRememberByWordId(int wordId){
        ContentValues values = new ContentValues();
        values.put(DatabaseContract.REMEMBERED, 1);
        String selection = "id = " + wordId;
        database.update(DatabaseContract.HISTORY_TABLE, values, selection, null);
    }

    public Cursor readHistory(){
        String[] column = {"id", DatabaseContract.SYNC_STATUS, DatabaseContract.DATE};
        Cursor cursor = database.query(DatabaseContract.HISTORY_TABLE, column, null, null, null, null, null);

        return cursor;
    }

    public void updateHistorySyncStatus(int pWordID, int pSyncStatus){
        ContentValues values = new ContentValues();
        values.put(DatabaseContract.SYNC_STATUS, pSyncStatus);
        String selection = "id = " + pWordID;
        database.update(DatabaseContract.HISTORY_TABLE, values, selection, null);
    }

    public void updateHistoryRemembered(int pWordID, int remembered){
        ContentValues values = new ContentValues();
        values.put(DatabaseContract.REMEMBERED, remembered);
        String selection = "id = " + pWordID;
        database.update(DatabaseContract.HISTORY_TABLE, values, selection, null);
    }

    public int removeHistory(int id){
        return database.delete("history", "id = " + id, null);
    }

    public long getHistoryWordsCount(){
        return DatabaseUtils.queryNumEntries(database, DatabaseContract.HISTORY_TABLE);
    }

    public String getDatetime(){
        java.text.SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }

    public int addFavorite(int pWordID, int pSyncStatus){
        ContentValues value = new ContentValues();
        value.put("id", pWordID);
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

    public ArrayList<Word> getFavoriteWordsToAlarm(){
        ArrayList<Word> wordList = new ArrayList<>();
        String query = "SELECT av.id, av.word, av.html, av.description, av.pronounce, favorite.remembered " +
                "FROM favorite JOIN av ON favorite.id = av.id WHERE favorite.remembered = 0";
        Cursor cursor = database.rawQuery(query, null);
        if(cursor.moveToFirst()){
            do{
                Word word = new Word();
                word.setId(cursor.getInt(0));
                word.setWord(cursor.getString(1));
                word.setHtml(cursor.getString(2));
                word.setDescription(cursor.getString(3));
                word.setPronounce(cursor.getString(4));
                word.setRemembered(cursor.getInt(5));
                wordList.add(word);
            }while (cursor.moveToNext());
        }
        cursor.close();
        return wordList;
    }

    public int removeFavorite(int id){
        return database.delete("favorite", "id = " + id, null);
    }

    public long getFavoriteWordsCount(){
        return DatabaseUtils.queryNumEntries(database, DatabaseContract.FAVORITE_TABLE);
    }

    public void setFavoriteRememberByWordId(int wordId){
        ContentValues values = new ContentValues();
        values.put(DatabaseContract.REMEMBERED, 1);
        String selection = "id = " + wordId;
        database.update(DatabaseContract.FAVORITE_TABLE, values, selection, null);
    }

    public void addRemindedWord(int wordId){
        ContentValues value = new ContentValues();
        value.put("wordId", wordId);
        database.insert(DatabaseContract.REMINDED_TABLE, null, value);
    }

    public void addRemindedWordDate(int wordId, long timeInMillis){
        ContentValues values = new ContentValues();
        values.put(DatabaseContract.WORD_ID, wordId);
        values.put(DatabaseContract.DATE, timeInMillis);
        database.insert(DatabaseContract.REMINDED_DATE_TABLE, null, values);
    }

    public Word getRemindedWordByWordId(int wordId){
        Word word = new Word();
        String query = "SELECT av.id, av.word, av.html, av.description, av.pronounce " +
                "FROM remindWord JOIN av ON remindWord.wordId = av.id WHERE av.id = " + wordId;
        Cursor cursor = database.rawQuery(query, null);
        if(cursor.moveToFirst()){
            word.setId(cursor.getInt(0));
            word.setWord(cursor.getString(1));
            word.setHtml(cursor.getString(2));
            word.setDescription(cursor.getString(3));
            word.setPronounce(cursor.getString(4));
        }
        cursor.close();
        return word;
    }

    public ArrayList<Word> getWordsByTopicId(int topicId) {
        ArrayList<Word> list = new ArrayList<>();
        Cursor cursor = database.rawQuery("SELECT * FROM av WHERE idTopic LIKE '%," + topicId + ",%'" + "LIMIT 100", null);
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

    public ArrayList<Topic> getTopics(){
        ArrayList<Topic> list = new ArrayList<>();
        Cursor cursor = database.rawQuery("SELECT * FROM topic", null);
        if(cursor.moveToFirst()){
            do{
                Topic topic = new Topic();
                topic.setTopicId(cursor.getInt(0));
                topic.setTopicName(cursor.getString(1));
                topic.setActive(cursor.getInt(2));

                list.add(topic);
            }while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    public int getWordCountByTopicId(int topicId){
        ArrayList<Word> wordList;
        wordList = getWordsRememberByTopicId(topicId);
        return wordList.size();
    }

    public void setTopicRemember(int wordId, int topicId){
        ContentValues values = new ContentValues();
        values.put(DatabaseContract.REMEMBERED, 1);
        String selection = "wordId = " + wordId + " AND " + "topicId = " + topicId;
        database.update(DatabaseContract.TOPIC_REMEMBER_TABLE, values, selection, null);
    }

    public ArrayList<Word> getWordsRememberByTopicId(int topicId) {
        ArrayList<Word> list = new ArrayList<>();
        Cursor cursor = database.rawQuery("SELECT av.id, av.word, av.html, av.description, av.pronounce, topicRemember.remembered " +
                                                "FROM topicRemember JOIN av on topicRemember.wordId = av.id " +
                                                "WHERE topicRemember.topicId = " + topicId, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Word word = new Word();
            word.setId(cursor.getInt(0));
            word.setWord(cursor.getString(1));
            word.setHtml(cursor.getString(2));
            word.setDescription(cursor.getString(3));
            word.setPronounce(cursor.getString(4));
            word.setRemembered(cursor.getInt(5));
            list.add(word);
            cursor.moveToNext();
        }
        cursor.close();
        return list;

    }

    public ArrayList<Word> getWordAlarmByTopicId(int topicId) {
        ArrayList<Word> list = new ArrayList<>();
        Cursor cursor = database.rawQuery("SELECT av.id, av.word, av.html, av.description, av.pronounce, topicRemember.remembered " +
                "FROM topicRemember JOIN av on topicRemember.wordId = av.id " +
                "WHERE topicRemember.topicId = " + topicId + " AND topicRemember.remembered <> 1", null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Word word = new Word();
            word.setId(cursor.getInt(0));
            word.setWord(cursor.getString(1));
            word.setHtml(cursor.getString(2));
            word.setDescription(cursor.getString(3));
            word.setPronounce(cursor.getString(4));
            word.setRemembered(cursor.getInt(5));
            list.add(word);
            cursor.moveToNext();
        }
        cursor.close();
        return list;

    }

    public ArrayList<MyDate> getRemindedWordDateById(int wordId){
        ArrayList<MyDate> dateList = new ArrayList<>();
        String query = "SELECT remindWord.wordId, remindWordDate.date FROM remindWord " +
                "JOIN remindWordDate ON remindWord.wordId = remindWordDate.wordId WHERE remindWord.wordId = " + wordId;
        Cursor cursor = database.rawQuery(query, null);
        if(cursor.moveToFirst()){
            do{
                MyDate date = new MyDate(cursor.getLong(1));
                dateList.add(date);
            }while (cursor.moveToNext());
        }
        cursor.close();
        return dateList;
    }

    public Question getQuestionById(int questionId){
        Question question = new Question();
        String query = "SELECT * FROM question WHERE questionId = " + questionId;
        Cursor cursor = database.rawQuery(query, null);
        if(cursor.moveToFirst()){
            question.setQuestionId(cursor.getInt(0));
            question.setTopicId(cursor.getInt(1));
            question.setQuestionDetail(cursor.getString(2));
        }
        cursor.close();
        return question;
    }

    public ArrayList<Choice> getChoicesByQuestionId(int questionId){
        ArrayList<Choice> choiceList = new ArrayList<>();
        String query = "SELECT question.questionId, question.topicId, " +
                "questionChoices.choiceId, questionChoices.wordId, questionChoices.isRight" +
                "  FROM question JOIN questionChoices ON question.questionId = questionChoices.questionId " +
                "WHERE question.questionId = " + questionId;
        Cursor cursor = database.rawQuery(query, null);
        if(cursor.moveToFirst()){
            do{
                Choice choice = new Choice();
                choice.setQuestionId(cursor.getInt(0));
                choice.setTopicId(cursor.getInt(1));
                choice.setChoiceId(cursor.getInt(2));
                choice.setWordId(cursor.getInt(3));
                choice.setIsRight(cursor.getInt(4));

                choiceList.add(choice);
            }while (cursor.moveToNext());
        }
        cursor.close();
        return choiceList;
    }
}
