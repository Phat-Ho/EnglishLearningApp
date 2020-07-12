package com.example.englishlearningapp.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.englishlearningapp.models.Choice;
import com.example.englishlearningapp.models.Game;
import com.example.englishlearningapp.models.HistoryGameWord;
import com.example.englishlearningapp.models.HistoryWord;
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

    public ArrayList<Word> getWordExactly(String word){
        ArrayList<Word> list = new ArrayList<>();
        Cursor cursor = database.rawQuery("SELECT * FROM av WHERE word LIKE '" + word + "'" + "LIMIT 100", null);
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
            word.setYoutubeLink(cursor.getString(cursor.getColumnIndex("YoutubeLink")));
        }
        cursor.close();
        return word;
    }

    public ArrayList<Word> getHistoryWordsWithoutDuplicateSortByAZ(){
        ArrayList<Word> wordList = new ArrayList<>();
        String query = "SELECT av.id, av.word, av.html, av.description, av.pronounce, av.YoutubeLink, history.date, history.remembered " +
                            " FROM history JOIN av on av.id = history.wordId GROUP BY history.wordId ORDER BY history.id";
        Cursor cursor = database.rawQuery(query, null);
        if(cursor.moveToFirst()){
            do{
                Word word = new Word();
                word.setId(cursor.getInt(0));
                word.setWord(cursor.getString(1));
                word.setHtml(cursor.getString(2));
                word.setDescription(cursor.getString(3));
                word.setPronounce(cursor.getString(4));
                word.setRemembered(cursor.getInt(7));
                word.setYoutubeLink(cursor.getString(5));
                wordList.add(word);
            }while (cursor.moveToNext());
        }
        cursor.close();
        return wordList;
    }

    public ArrayList<Word> getHistoryWordsWithoutDuplicateSortByTimeLatest(){
        ArrayList<Word> wordList = new ArrayList<>();
        String query = "SELECT av.id, av.word, av.html, av.description, av.pronounce, history.date, history.remembered" +
                " FROM history JOIN av on av.id = history.wordId GROUP BY history.wordId ORDER BY history.date DESC";
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

    public void clearHistory(){
        database.delete(DatabaseContract.HISTORY_TABLE, null, null);
    }

    public void clearFavorite(){
        database.delete(DatabaseContract.FAVORITE_TABLE, null, null);
    }

    public void clearRemembered(){
        database.delete(DatabaseContract.REMEMBERED_TABLE, null, null);
    }

    public ArrayList<Word> getHistoryWordsWithDuplicateSortByAZ(){
        ArrayList<Word> wordList = new ArrayList<>();
        String query = "SELECT av.id, av.word, av.html, av.description, av.pronounce, history.date, history.remembered " +
                        "FROM history JOIN av on av.id = history.wordId ORDER BY history.wordId";
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

    public ArrayList<Word> getHistoryWordsWithDuplicateSortByTimeLatest(){
        ArrayList<Word> wordList = new ArrayList<>();
        String query = "SELECT av.id, av.word, av.html, av.description, av.pronounce, history.date, history.remembered " +
                "FROM history JOIN av on av.id = history.wordId ORDER BY history.date DESC";
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
                " FROM history JOIN av on av.id = history.wordId WHERE history.remembered = 0 GROUP BY history.wordId";
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

    public Word getHistoryWordByWordId(int wordId){
        Word word = new Word();
        String query = "SELECT av.id, av.word, av.html, av.description, av.pronounce, history.date, history.remembered " +
                " FROM history JOIN av on av.id = history.wordId WHERE history.wordId = " + wordId + " GROUP BY history.wordId";
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

    public HistoryWord getHistoryByWordId(int wordId){
        HistoryWord historyWord = new HistoryWord();
        String query = "SELECT * from history WHERE wordId = " + wordId + " GROUP BY wordId";
        Cursor cursor = database.rawQuery(query, null);
        if(cursor.moveToFirst()){
            historyWord.setId(cursor.getInt(cursor.getColumnIndex(DatabaseContract.ID)));
            historyWord.setRemembered(cursor.getInt(cursor.getColumnIndex(DatabaseContract.REMEMBERED)));
            historyWord.setSearchTime(cursor.getInt(cursor.getColumnIndex(DatabaseContract.DATE)));
            historyWord.setWordId(cursor.getInt(cursor.getColumnIndex(DatabaseContract.WORD_ID)));
            historyWord.setUserId(cursor.getInt(cursor.getColumnIndex(DatabaseContract.ID_USER)));
            historyWord.setSynchronized(cursor.getInt(cursor.getColumnIndex(DatabaseContract.SYNCHRONIZED)));
            historyWord.setLinkWeb(cursor.getString(cursor.getColumnIndex(DatabaseContract.LINK_WEB)));
            historyWord.setIsChange(cursor.getInt(cursor.getColumnIndex(DatabaseContract.IS_CHANGE)));
            historyWord.setIdServer(cursor.getInt(cursor.getColumnIndex(DatabaseContract.ID_SERVER)));
        }
        cursor.close();
        return historyWord;
    }

    public HistoryWord getFavoriteByWordId(int wordId){
        HistoryWord favoriteWord = new HistoryWord();
        String query = "SELECT * from favorite WHERE wordId = " + wordId;
        Cursor cursor = database.rawQuery(query, null);
        if(cursor.moveToFirst()){
            favoriteWord.setId(cursor.getInt(cursor.getColumnIndex(DatabaseContract.ID)));
            favoriteWord.setRemembered(cursor.getInt(cursor.getColumnIndex(DatabaseContract.REMEMBERED)));
            favoriteWord.setWordId(cursor.getInt(cursor.getColumnIndex(DatabaseContract.WORD_ID)));
            favoriteWord.setUserId(cursor.getInt(cursor.getColumnIndex(DatabaseContract.ID_USER)));
            favoriteWord.setIsChange(cursor.getInt(cursor.getColumnIndex(DatabaseContract.IS_CHANGE)));
            favoriteWord.setIdServer(cursor.getInt(cursor.getColumnIndex(DatabaseContract.ID_SERVER)));
        }
        cursor.close();
        return favoriteWord;
    }

    public long addHistory(int pWordID, long pDate, int userId, int remembered, int idServer){
        ContentValues value = new ContentValues();
        value.put(DatabaseContract.WORD_ID, pWordID);
        value.put(DatabaseContract.DATE, pDate);
        value.put(DatabaseContract.REMEMBERED, remembered);
        value.put(DatabaseContract.SYNCHRONIZED, 0);
        value.put(DatabaseContract.LINK_WEB, "");
        value.put(DatabaseContract.IS_CHANGE, 0);
        value.put(DatabaseContract.ID_SERVER, idServer);
        value.put(DatabaseContract.ID_USER, userId);
        return database.insert("history", null, value);
    }

    public ArrayList<MyDate> getHistoryDateByWordId(int wordId){
        ArrayList<MyDate> dateList = new ArrayList<>();
        String query = "SELECT history.wordId, history.date FROM history WHERE history.wordId = " + wordId;
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

    public void updateHistoryIdServer(long id, int idServer){
        ContentValues values = new ContentValues();
        values.put(DatabaseContract.ID_SERVER, idServer);
        String selection = "id = " + id;
        database.update(DatabaseContract.HISTORY_TABLE, values, selection, null);
    }

    public void updateHistoryIdUser(int idUser){
        ContentValues values = new ContentValues();
        values.put(DatabaseContract.ID_USER, idUser);
        database.update(DatabaseContract.HISTORY_TABLE, values, null, null);
    }

    public void updateHistoryIsChange(int id){
        ContentValues values = new ContentValues();
        values.put(DatabaseContract.IS_CHANGE, 0);
        String selection = "id = " + id;
        database.update(DatabaseContract.HISTORY_TABLE, values, selection, null);
    }

    public void updateFavoriteIsChange(int id){
        ContentValues values = new ContentValues();
        values.put(DatabaseContract.IS_CHANGE, 0);
        String selection = "id = " + id;
        database.update(DatabaseContract.FAVORITE_TABLE, values, selection, null);
    }

    public void setHistoryRememberByWordId(int wordId){
        ContentValues values = new ContentValues();
        values.put(DatabaseContract.REMEMBERED, 1);
        values.put(DatabaseContract.IS_CHANGE, 1);
        String selection = "wordId = " + wordId;
        database.update(DatabaseContract.HISTORY_TABLE, values, selection, null);
    }

    public HistoryWord getHistoryWordByIdServer(int idServer){
        HistoryWord historyWord = new HistoryWord();
        String query = "SELECT * from history WHERE IdServer = " + idServer;
        Cursor cursor = database.rawQuery(query, null);
        if(cursor.moveToFirst()){
            historyWord.setId(cursor.getInt(cursor.getColumnIndex(DatabaseContract.ID)));
            historyWord.setRemembered(cursor.getInt(cursor.getColumnIndex(DatabaseContract.REMEMBERED)));
            historyWord.setWordId(cursor.getInt(cursor.getColumnIndex(DatabaseContract.WORD_ID)));
            historyWord.setUserId(cursor.getInt(cursor.getColumnIndex(DatabaseContract.ID_USER)));
            historyWord.setIsChange(cursor.getInt(cursor.getColumnIndex(DatabaseContract.IS_CHANGE)));
            historyWord.setIdServer(cursor.getInt(cursor.getColumnIndex(DatabaseContract.ID_SERVER)));
        }else{
            historyWord.setId(0);
        }
        cursor.close();
        return historyWord;
    }

    public HistoryWord getFavoriteWordByIdServer(int idServer){
        HistoryWord favoriteWord = new HistoryWord();
        String query = "SELECT * from favorite WHERE IdServer = " + idServer;
        Cursor cursor = database.rawQuery(query, null);
        if(cursor.moveToFirst()){
            favoriteWord.setId(cursor.getInt(cursor.getColumnIndex(DatabaseContract.ID)));
            favoriteWord.setRemembered(cursor.getInt(cursor.getColumnIndex(DatabaseContract.REMEMBERED)));
            favoriteWord.setWordId(cursor.getInt(cursor.getColumnIndex(DatabaseContract.WORD_ID)));
            favoriteWord.setUserId(cursor.getInt(cursor.getColumnIndex(DatabaseContract.ID_USER)));
            favoriteWord.setIsChange(cursor.getInt(cursor.getColumnIndex(DatabaseContract.IS_CHANGE)));
            favoriteWord.setIdServer(cursor.getInt(cursor.getColumnIndex(DatabaseContract.ID_SERVER)));
        }else{
            favoriteWord.setId(0);
        }
        cursor.close();
        return favoriteWord;
    }

    public Cursor readHistory(){
        String[] column = {DatabaseContract.ID, DatabaseContract.WORD_ID, DatabaseContract.DATE,
                            DatabaseContract.ID_SERVER, DatabaseContract.ID_USER, DatabaseContract.REMEMBERED,
                            DatabaseContract.LINK_WEB, DatabaseContract.SYNCHRONIZED, DatabaseContract.IS_CHANGE};
        Cursor cursor = database.query(DatabaseContract.HISTORY_TABLE, column, null, null, null, null, null);

        return cursor;
    }

    public Cursor readFavorite(){
        String[] column = {DatabaseContract.ID, DatabaseContract.WORD_ID, DatabaseContract.ID_SERVER, DatabaseContract.ID_USER, DatabaseContract.REMEMBERED,
                DatabaseContract.SYNCHRONIZED, DatabaseContract.IS_CHANGE};
        Cursor cursor = database.query(DatabaseContract.FAVORITE_TABLE, column, null, null, null, null, null);

        return cursor;
    }

    public int removeHistory(int id){
        return database.delete("history", "wordId = " + id, null);
    }

    public long getHistoryWordsCount(){
        String query = "SELECT * FROM history GROUP BY wordId";
        Cursor cursor = database.rawQuery(query, null);
        return cursor.getCount();
//        return DatabaseUtils.queryNumEntries(database, DatabaseContract.HISTORY_TABLE);
    }

    public String getDatetime(){
        java.text.SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }

    public long addFavorite(int pWordID, int userId, int remembered, int idServer){
        ContentValues value = new ContentValues();
        value.put(DatabaseContract.WORD_ID, pWordID);
        value.put(DatabaseContract.ID_SERVER, idServer);
        value.put(DatabaseContract.REMEMBERED, remembered);
        value.put(DatabaseContract.ID_USER, userId);
        value.put(DatabaseContract.IS_CHANGE, 0);
        return database.insert("favorite", null, value);
    }

    public void updateFavoriteIdServer(long id, int idServer){
        ContentValues values = new ContentValues();
        values.put(DatabaseContract.ID_SERVER, idServer);
        String selection = "id = " + id;
        database.update(DatabaseContract.FAVORITE_TABLE, values, selection, null);
    }

    public ArrayList<Word> getFavoriteWords(){
        ArrayList<Word> wordList = new ArrayList<>();
        String query = "SELECT av.id, av.word, av.html, av.description, av.pronounce, av.YoutubeLink " +
                "FROM favorite JOIN av ON favorite.wordId = av.id";
        Cursor cursor = database.rawQuery(query, null);
        if(cursor.moveToFirst()){
            do{
                Word word = new Word();
                word.setId(cursor.getInt(0));
                word.setWord(cursor.getString(1));
                word.setHtml(cursor.getString(2));
                word.setDescription(cursor.getString(3));
                word.setPronounce(cursor.getString(4));
                word.setYoutubeLink(cursor.getString(5));
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
        return database.delete("favorite", "wordId = " + id, null);
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

    public void addRemindedWord(int wordId, long datetime){
        ContentValues value = new ContentValues();
        value.put(DatabaseContract.WORD_ID, wordId);
        value.put(DatabaseContract.DATE, datetime);
        database.insert(DatabaseContract.REMINDED_TABLE, null, value);
    }

    public void addRememberedWord(int wordId, long date){
        ContentValues values = new ContentValues();
        values.put(DatabaseContract.WORD_ID, wordId);
        values.put(DatabaseContract.DATE, date);
        database.insert(DatabaseContract.REMEMBERED_TABLE, null, values);
    }

    public ArrayList<Word> getAllRememberedWords(){
        ArrayList<Word> list = new ArrayList<>();
        String query = "SELECT * FROM av JOIN remembered ON av.id = remembered.wordId";
        Cursor cursor = database.rawQuery(query, null);
        if(cursor.moveToFirst()){
            do {
                Word word = new Word();
                word.setId(cursor.getInt(0));
                word.setWord(cursor.getString(1));
                word.setHtml(cursor.getString(2));
                word.setDescription(cursor.getString(3));
                word.setPronounce(cursor.getString(4));

                list.add(word);
            }while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    public Word getRememberedWordByWordId(int wordId){
        Word word = new Word();
        String query = "SELECT * FROM av JOIN remembered ON av.id = remembered.wordId WHERE av.id = " + wordId;
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
        Cursor cursor = database.rawQuery("SELECT * FROM av WHERE idTopic LIKE '%," + topicId + ",%'" + "LIMIT 100", null);
        return cursor.getCount();
    }

    public long setTopicRemember(int wordId, int topicId){
        ContentValues values = new ContentValues();
        values.put(DatabaseContract.WORD_ID, wordId);
        values.put(DatabaseContract.TOPIC_ID, topicId);
        return database.insert(DatabaseContract.TOPIC_REMEMBER_TABLE, null, values);
    }

    public ArrayList<Word> getWordsRememberByTopicId(int topicId) {
        ArrayList<Word> list = new ArrayList<>();
        String query = "SELECT av.id, av.word, av.html, av.description, av.pronounce, av.YoutubeLink, topicRemember.wordId " +
                "FROM av LEFT JOIN topicRemember ON av.id = topicRemember.wordId " +
                "WHERE av.idTopic like '%," + topicId + ",%' AND topicRemember.wordId NOT NULL";
        Cursor cursor = database.rawQuery(query, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Word word = new Word();
            word.setId(cursor.getInt(0));
            word.setWord(cursor.getString(1));
            word.setHtml(cursor.getString(2));
            word.setDescription(cursor.getString(3));
            word.setPronounce(cursor.getString(4));
            word.setYoutubeLink(cursor.getString(5));
            word.setRemembered(1);
            list.add(word);
            cursor.moveToNext();
        }
        cursor.close();
        return list;

    }

    public ArrayList<Word> getWordAlarmByTopicId(int topicId) {
        ArrayList<Word> list = new ArrayList<>();
        String query = "SELECT av.id, av.word, av.html, av.description, av.pronounce, av.YoutubeLink, topicRemember.wordId " +
                "FROM av LEFT JOIN topicRemember ON av.id = topicRemember.wordId " +
                "WHERE av.idTopic like '%," + topicId + ",%' AND topicRemember.wordId IS NULL";
        Cursor cursor = database.rawQuery(query, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Word word = new Word();
            word.setId(cursor.getInt(0));
            word.setWord(cursor.getString(1));
            word.setHtml(cursor.getString(2));
            word.setDescription(cursor.getString(3));
            word.setPronounce(cursor.getString(4));
            word.setYoutubeLink(cursor.getString(5));
            word.setRemembered(0);
            list.add(word);
            cursor.moveToNext();
        }
        cursor.close();
        return list;
    }

    public ArrayList<MyDate> getRemindedWordDateById(int wordId){
        ArrayList<MyDate> dateList = new ArrayList<>();
        String query = "SELECT remindWord.wordId, remindWord.date FROM remindWord WHERE remindWord.wordId = " + wordId;
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

    public ArrayList<Question> getAllQuestion(){
        ArrayList<Question> questionList = new ArrayList<>();
        String query = "SELECT * FROM question";
        Cursor cursor = database.rawQuery(query, null);
        if(cursor.moveToFirst()){
            do{
                Question question = new Question();
                question.setQuestionId(cursor.getInt(0));
                question.setTopicId(cursor.getInt(1));
                question.setQuestionDetail(cursor.getString(2));

                questionList.add(question);
            }while (cursor.moveToNext());
        }
        cursor.close();

        return questionList;
    }

    public Topic getTopicByQuestionId(int questId){
        Topic topic = new Topic();
        String query = "SELECT topic.id, topic.topicName FROM question " +
                "JOIN topic ON question.topicId = topic.id " +
                "WHERE questionId = " + questId;
        Cursor cursor = database.rawQuery(query, null);
        if(cursor.moveToFirst()){
            topic.setTopicId(cursor.getInt(0));
            topic.setTopicName(cursor.getString(1));
        }
        cursor.close();

        return topic;
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

    public ArrayList<Topic> getAllTopic(){
        ArrayList<Topic> topicList = new ArrayList<>();
        String query = "SELECT * FROM topic WHERE active = 1";
        Cursor cursor = database.rawQuery(query, null);
        if(cursor.moveToFirst()){
            do{
                Topic topic = new Topic();
                topic.setTopicId(cursor.getInt(0));
                topic.setTopicName(cursor.getString(1));
                topic.setTopicNameVie(cursor.getString(2));
                topic.setActive(cursor.getInt(3));
                topic.setIdServer(cursor.getInt(4));
                topic.setTopicImage("https://imperia.edu.my/wp-content/uploads/2019/12/english.jpg");
                topicList.add(topic);
            }while (cursor.moveToNext());
        }
        cursor.close();

        return topicList;
    }

    public Topic getTopicById(int id){
        Topic topic = new Topic();
        String query = "SELECT * FROM topic WHERE id = " + id + " AND active = 1";
        Cursor cursor = database.rawQuery(query, null);
        if(cursor.moveToFirst()){
            topic.setTopicId(cursor.getInt(0));
            topic.setTopicName(cursor.getString(1));
            topic.setTopicNameVie(cursor.getString(2));
            topic.setActive(cursor.getInt(3));
            topic.setIdServer(cursor.getInt(4));
            topic.setTopicImage("https://imperia.edu.my/wp-content/uploads/2019/12/english.jpg");
        }
        cursor.close();

        return topic;
    }

    public long addGame(long date, String roomName){
        ContentValues values = new ContentValues();
        values.put(DatabaseContract.DATE, date);
        values.put("roomName", roomName);
        return database.insert("game_history", null, values);
    }

    public long addGameWord(long gameId, String word, String playerName){
        ContentValues values = new ContentValues();
        values.put("gameId", gameId);
        values.put("word", word);
        values.put("player_name", playerName);
        return database.insert("game_history_detail", null, values);
    }

    public ArrayList<Game> getAllGame(){
        ArrayList<Game> gameList = new ArrayList<>();
        String query = "SELECT * FROM game_history";
        Cursor cursor = database.rawQuery(query, null);
        if(cursor.moveToFirst()){
            do{
                int id = cursor.getInt(cursor.getColumnIndex("id"));
                long gameDate = cursor.getLong(cursor.getColumnIndex("date"));
                String roomName = cursor.getString(cursor.getColumnIndex("roomName"));
                Game game = new Game(id, gameDate, roomName);
                gameList.add(game);
            }while (cursor.moveToNext());
        }
        cursor.close();

        return gameList;
    }

    public ArrayList<HistoryGameWord> getGameDetailById(int pId){
        ArrayList<HistoryGameWord> gameWords = new ArrayList<>();
        String query = "SELECT * FROM game_history_detail WHERE gameId = " + pId;
        Cursor cursor = database.rawQuery(query, null);
        if(cursor.moveToFirst()){
            do{
                int id = cursor.getInt(cursor.getColumnIndex("id"));
                int gameId = cursor.getInt(cursor.getColumnIndex("gameId"));
                String word = cursor.getString(cursor.getColumnIndex("word"));
                String playerName = cursor.getString(cursor.getColumnIndex("player_name"));
                HistoryGameWord gameWord = new HistoryGameWord(id, gameId, word, playerName);
                gameWords.add(gameWord);
            }while (cursor.moveToNext());
        }
        cursor.close();

        return gameWords;
    }
}
