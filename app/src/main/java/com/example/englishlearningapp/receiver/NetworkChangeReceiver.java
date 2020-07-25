package com.example.englishlearningapp.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.englishlearningapp.models.Topic;
import com.example.englishlearningapp.utils.DatabaseAccess;
import com.example.englishlearningapp.utils.DatabaseContract;
import com.example.englishlearningapp.utils.LoginManager;
import com.example.englishlearningapp.utils.Server;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class NetworkChangeReceiver extends BroadcastReceiver {
    private static final String TAG = "NetworkChangeReceiver";
    LoginManager loginManager;
    boolean isConnected;


    @Override
    public void onReceive(Context context, Intent intent) {
        loginManager = new LoginManager(context);
        boolean isLogin = loginManager.isLogin();
        final int userID = loginManager.getUserId();
        Log.d(TAG, "isLogin: " + isLogin);
        if(ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction()) && isLogin == true){
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            isConnected = networkInfo != null && networkInfo.isConnected();
            if(!isConnected){
                Log.d(TAG, "onReceive: Disconnected");
            }else{
                Log.d(TAG, "onReceive: Connected");
                final DatabaseAccess databaseAccess = DatabaseAccess.getInstance(context);
                databaseAccess.open();

                SyncDatabase(databaseAccess, context, userID);
            }
        }else{
            Log.d(TAG, "onReceive: no login");
        }
    }


    private void SyncDatabase(final DatabaseAccess databaseAccess, final Context context, int userID) {
        //Request JSON array body
        JSONArray bodyJson = new JSONArray();
        JSONArray listIdServerHistoryArray = new JSONArray();
        JSONArray dataHistoryArray = new JSONArray();

        //Send data to server
        //Get the history cursor from local database
        Cursor cursor = databaseAccess.readHistory();
        if(cursor.moveToFirst()){
            do{
                int idServer = cursor.getInt(cursor.getColumnIndex("IdServer"));
                int isChange = cursor.getInt(cursor.getColumnIndex("IsChange"));
                if(idServer == 0 || isChange == 1) { //Sync if the history word is not saved to server
                    final long dateTimeInMillis = cursor.getLong(cursor.getColumnIndex(DatabaseContract.DATE));
                    Log.d(TAG, "datetime: " + dateTimeInMillis);
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss", getCurrentLocale(context));
                    String dateString = simpleDateFormat.format(dateTimeInMillis);
                    JSONObject dataObject = new JSONObject();
                    try {
                        dataObject.put("Id", cursor.getInt(cursor.getColumnIndex("id")));
                        dataObject.put("IdUser", cursor.getInt(cursor.getColumnIndex("IdUser")));
                        dataObject.put("IdWord", cursor.getInt(cursor.getColumnIndex("wordId")));
                        dataObject.put("Remembered", cursor.getInt(cursor.getColumnIndex("remembered")));
                        dataObject.put("Synchronized", cursor.getInt(cursor.getColumnIndex("Synchronized")));
                        dataObject.put("TimeSearch", dateString);
                        dataObject.put("LinkWeb", cursor.getString(cursor.getColumnIndex("LinkWeb")));
                        dataObject.put("IsChange", cursor.getInt(cursor.getColumnIndex("IsChange")));
                        dataObject.put("IdServer", cursor.getInt(cursor.getColumnIndex("IdServer")));
                        dataHistoryArray.put(dataObject);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else{
                    listIdServerHistoryArray.put(idServer);
                }
            }while (cursor.moveToNext());
        }

        JSONObject tableHistory = new JSONObject();
        try {
            tableHistory.put("table", "SearchHistory");
            tableHistory.put("data", dataHistoryArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        bodyJson.put(tableHistory);

        //Get the favorite cursor from local database
        JSONArray dataFavoriteArray = new JSONArray();
        JSONArray listIdServerFavoriteArray = new JSONArray();
        Cursor favoriteCursor = databaseAccess.readFavorite();
        if(favoriteCursor.moveToFirst()){
            do{
                int idServer = favoriteCursor.getInt(favoriteCursor.getColumnIndex("IdServer"));
                int isChange = favoriteCursor.getInt(favoriteCursor.getColumnIndex("IsChange"));
                if(idServer == 0 || isChange == 1) { //Sync if the favorite word is not saved to server or had been change
                    JSONObject dataObject = new JSONObject();

                    try {
                        dataObject.put("Id", favoriteCursor.getInt(favoriteCursor.getColumnIndex(DatabaseContract.ID)));
                        dataObject.put("IdUser", favoriteCursor.getInt(favoriteCursor.getColumnIndex(DatabaseContract.ID_USER)));
                        dataObject.put("IdWord", favoriteCursor.getInt(favoriteCursor.getColumnIndex(DatabaseContract.WORD_ID)));
                        dataObject.put("Remembered", favoriteCursor.getInt(favoriteCursor.getColumnIndex(DatabaseContract.REMEMBERED)));
                        dataObject.put("Synchronized", favoriteCursor.getInt(favoriteCursor.getColumnIndex(DatabaseContract.SYNCHRONIZED)));
                        dataObject.put("IsChange", favoriteCursor.getInt(favoriteCursor.getColumnIndex(DatabaseContract.IS_CHANGE)));
                        dataObject.put("IdServer", favoriteCursor.getInt(favoriteCursor.getColumnIndex(DatabaseContract.ID_SERVER)));
                        dataFavoriteArray.put(dataObject);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else{
                    listIdServerFavoriteArray.put(idServer);
                }
            }while (favoriteCursor.moveToNext());
        }

        JSONObject favoriteTable = new JSONObject();
        try {
            favoriteTable.put("table", "WordLike");
            favoriteTable.put("data", dataFavoriteArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        bodyJson.put(favoriteTable);

        //Get topic remember cursor from database
        Cursor topicCursor = databaseAccess.readTopicRemember();
        JSONArray dataTopicArray = new JSONArray();
        JSONArray listIdServerTopicArray = new JSONArray();
        if(topicCursor.moveToFirst()){
            do{
                int idServer = topicCursor.getInt(topicCursor.getColumnIndex("IdServer"));
                int isChange = topicCursor.getInt(topicCursor.getColumnIndex("IsChange"));
                if(idServer == 0 || isChange == 1) { //Sync if the topic word is not saved to server or had been change
                    JSONObject dataObject = new JSONObject();

                    try {
                        dataObject.put("Id", topicCursor.getInt(topicCursor.getColumnIndex(DatabaseContract.ID)));
                        dataObject.put("IdUser", loginManager.getUserId());
                        dataObject.put("IdWord", topicCursor.getInt(topicCursor.getColumnIndex(DatabaseContract.WORD_ID)));
                        dataObject.put("IdTopic", topicCursor.getInt(topicCursor.getColumnIndex(DatabaseContract.TOPIC_ID)));
                        dataObject.put("IsRemember", topicCursor.getInt(topicCursor.getColumnIndex(DatabaseContract.REMEMBERED)));
                        dataObject.put("IsChange", topicCursor.getInt(topicCursor.getColumnIndex(DatabaseContract.IS_CHANGE)));
                        dataObject.put("IdServer", topicCursor.getInt(topicCursor.getColumnIndex(DatabaseContract.ID_SERVER)));
                        dataTopicArray.put(dataObject);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else{
                    listIdServerTopicArray.put(idServer);
                }
            }while (topicCursor.moveToNext());
        }

        JSONObject topicTable = new JSONObject();
        try {
            topicTable.put("table", "TopicRemember");
            topicTable.put("data", dataTopicArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        bodyJson.put(topicTable);

        //Sync topic table
        /*JSONArray listIdServerTopicArray1 = new JSONArray();
        ArrayList<Topic> topicList = databaseAccess.getTopics();
        for (Topic topic:topicList) {
            listIdServerTopicArray1.put(topic.getIdServer());
        }*/
        Log.d(TAG, "request body array: " + bodyJson);
        //End initial body json


        //Begin cal send API
        String url = Server.SEND_DATA_URL;
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        JsonArrayRequest sendDataRequest = new JsonArrayRequest(Request.Method.POST, url, bodyJson, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Log.d(TAG, "onResponse send data: " + response);
                int resLength = response.length();
                if(resLength == 0){
                    //Do nothing
                }else{
                    for (int i = 0; i < resLength; i++) {
                        try {
                            JSONObject tableObject = (JSONObject) response.get(i);
                            String tableName = tableObject.getString("table");
                            JSONArray array = (JSONArray) tableObject.get("data");
                            if(tableName.equals("SearchHistory")){
                                int length = array.length();
                                for (int j = 0; j < length; j++) {
                                    JSONObject data = (JSONObject) array.get(j);
                                    int idServer = data.getInt("Id");
                                    int historyId = data.getInt("IdWord");
                                    databaseAccess.updateHistoryIdServer(historyId, idServer);
                                }
                            }
                            if(tableName.equals("WordLike")){
                                int length = array.length();
                                for (int j = 0; j < length; j++) {
                                    JSONObject data = (JSONObject) array.get(j);
                                    int idServer = data.getInt("Id");
                                    int favoriteId = data.getInt("IdWord");
                                    databaseAccess.updateFavoriteIdServer(favoriteId, idServer);
                                }
                            }
                            /*if(tableName.equals("TopicRemember")){
                                int length = array.length();
                                for(int j = 0; j< length; j++){
                                    JSONObject data = (JSONObject)array.get(j);
                                    int idServer = data.getInt("IdServer");
                                    int topicRememberId = data.getInt("Id");
                                    databaseAccess.updateTopicRememberIdServer(topicRememberId, idServer);
                                }
                            }*/
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(error != null){
                    Log.e("Error send data: ", error.getMessage() == null ? "null pointer" : error.getMessage());
                }
            }
        });
        requestQueue.add(sendDataRequest);
        //End send data to server


        //Get data from server request
        //Initial body json
        JSONArray getDataBodyArray = new JSONArray();
        JSONObject getHistoryJSON = new JSONObject();
        JSONObject getFavoriteJSON = new JSONObject();
        JSONObject getTopicRemember = new JSONObject();
        JSONObject getTopic = new JSONObject();
        JSONObject getAV = new JSONObject();
        try {
            getHistoryJSON.put("table", "SearchHistory");
            getHistoryJSON.put("listIds", listIdServerHistoryArray);
            getDataBodyArray.put(getHistoryJSON);
            getFavoriteJSON.put("table", "WordLike");
            getFavoriteJSON.put("listIds", listIdServerFavoriteArray);
            getDataBodyArray.put(getFavoriteJSON);
            getTopicRemember.put("table", "TopicRemember");
            getTopicRemember.put("listIds", listIdServerTopicArray);
            getDataBodyArray.put(getTopicRemember);
            /*getTopic.put("table", "Topic");
            getTopic.put("listIds", listIdServerTopicArray1);
            getDataBodyArray.put(getTopic);*/
            Log.d(TAG, "getDataBody: " + getDataBodyArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //Begin call get data API
        String getDataUrl = Server.GET_DATA_URL + userID;
        JsonArrayRequest getDataRequest = new JsonArrayRequest(Request.Method.POST, getDataUrl, getDataBodyArray, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Log.d(TAG, "get Data response: " + response.toString());
                int resLength = response.length();
                for (int i = 0; i < resLength; i++) {
                    try {
                        JSONObject tableObject = (JSONObject) response.get(i);
                        String tableName = tableObject.getString("table");
                        JSONArray dataArray = (JSONArray) tableObject.get("data");
                        int length = dataArray.length();
                        if(tableName.equals("SearchHistory")){
                            if(length == 0){
                                //Do nothing
                            }else{
                                for(int j = 0;j < length;j++){
                                    JSONObject dataObject = (JSONObject) dataArray.get(j);
                                    int isChange = dataObject.getInt("IsChange");
                                    int wordId = dataObject.getInt("IdWord");
                                    int userId = dataObject.getInt("IdUser");
                                    int isRemembered = dataObject.getInt("Remembered");
                                    int idServer = dataObject.getInt("Id");
                                    String timeSearch = dataObject.getString("TimeSearch");
                                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", getCurrentLocale(context));
                                    simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
                                    Date date = simpleDateFormat.parse(timeSearch);
                                    Log.d(TAG, "date: " + date + " time search: " + timeSearch);
                                    String location = dataObject.getString("location");
                                    if(isChange == 0){
                                        databaseAccess.addHistory(wordId, date.getTime(), userId, isRemembered, idServer, location);
                                    }
                                    if(isChange == 1){
                                        if(databaseAccess.getHistoryWordByIdServer(idServer).getId() == 0){
                                            databaseAccess.addHistory(wordId, date.getTime(), userId, isRemembered, idServer, location);
                                        }
                                        databaseAccess.setHistoryRememberByWordId(wordId);
                                    }

                                    if(isRemembered == 1){
                                        if(databaseAccess.getRememberedWordByWordId(wordId).getId() == 0){
                                            databaseAccess.addRememberedWord(wordId, 0);
                                        }
                                    }
                                }
                            }
                        }

                        if(tableName.equals("WordLike")){
                            if(length == 0){
                                //Do nothing
                            }else{
                                for(int j = 0;j < length;j++){
                                    JSONObject dataObject = (JSONObject) dataArray.get(j);
                                    int isChange = dataObject.getInt("IsChange");
                                    int wordId = dataObject.getInt("IdWord");
                                    int userId = dataObject.getInt("IdUser");
                                    int isRemembered = dataObject.getInt("Remembered");
                                    int idServer = dataObject.getInt("Id");

                                    if(isChange == 0){
                                        databaseAccess.addFavorite(wordId, userId, isRemembered, idServer);
                                    }

                                    if(isChange == 1){
                                        if(databaseAccess.getFavoriteWordByIdServer(idServer).getId() == 0){
                                            databaseAccess.addFavorite(wordId, userId, isRemembered, idServer);
                                        }else{
                                            databaseAccess.setFavoriteRememberByWordId(wordId);
                                        }
                                    }

                                    if(isRemembered == 1){
                                        if(databaseAccess.getRememberedWordByWordId(wordId).getId() == 0){
                                            databaseAccess.addRememberedWord(wordId, 0);
                                        }
                                    }
                                }
                            }
                        }

                        if(tableName.equals("TopicRemember")){
                            if(length == 0){
                                //Do nothing
                            }else{
                                for(int j = 0;j < length;j++){
                                    JSONObject dataObject = (JSONObject) dataArray.get(j);
                                    int isChange = dataObject.getInt("IsChange");
                                    int wordId = dataObject.getInt("IdWord");
                                    int topicId = dataObject.getInt("IdTopic");
                                    int isRemembered = dataObject.getInt("IsRemember");
                                    int idServer = dataObject.getInt("Id");
                                    if(isChange == 1){
                                        if(databaseAccess.getTopicRememberWordByIdServer(idServer).getId() != 0){
                                            databaseAccess.setTopicRemember(wordId, topicId, idServer);
                                        }
                                    }

                                    if(isRemembered == 1){
                                        if(databaseAccess.getRememberedWordByWordId(wordId).getId() == 0){
                                            databaseAccess.addRememberedWord(wordId, 0);
                                        }
                                    }
                                }
                            }
                        }

                        if(tableName.equals("Topic")){
                            if(length == 0){
                                //Do nothing
                            }else{
                                for(int j = 0;j < length;j++){
                                    JSONObject dataObject = (JSONObject) dataArray.get(j);
                                    String name = dataObject.getString("NameTopic");
                                    String nameVie = dataObject.getString("Translate");
                                    String image = dataObject.getString("Image");
                                    int idServer = dataObject.getInt("Id");
                                    Topic topic = new Topic();
                                    topic.setTopicId(idServer);
                                    topic.setTopicName(name);
                                    topic.setTopicNameVie(nameVie);
                                    topic.setTopicImage(image);
                                    databaseAccess.addTopic(topic);
                                }
                            }
                        }

                    } catch (JSONException | ParseException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(error != null){
                    Log.e("Error: ", error.getMessage() == null ? "null pointer" : error.getMessage());
                }
            }
        });
        requestQueue.add(getDataRequest);
        //End get data from server
    }

    Locale getCurrentLocale(Context context){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            return context.getResources().getConfiguration().getLocales().get(0);
        } else{
            //noinspection deprecation
            return context.getResources().getConfiguration().locale;
        }
    }
}
