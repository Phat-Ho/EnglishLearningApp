package com.example.englishlearningapp.receiver;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.ContactsContract;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.englishlearningapp.utils.DatabaseAccess;
import com.example.englishlearningapp.utils.DatabaseContract;
import com.example.englishlearningapp.utils.LoginManager;
import com.example.englishlearningapp.utils.Server;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

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

                //Request JSON array body
                JSONArray bodyJson = new JSONArray();
                JSONArray dataJsonArray = new JSONArray();
                //Get the history cursor from local database
                Cursor cursor = databaseAccess.readHistory();
                if(cursor.moveToFirst()){
                    do{
                        int idServer = cursor.getInt(cursor.getColumnIndex("IdServer"));
                        if(idServer == 0) { //Sync if the history word is not saved to server
                            final long dateTimeInMillis = cursor.getLong(cursor.getColumnIndex(DatabaseContract.DATE));
                            Log.d(TAG, "datetime: " + dateTimeInMillis);
                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
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
                                dataJsonArray.put(dataObject);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }while (cursor.moveToNext());
                }

                JSONObject tableObject = new JSONObject();
                try {
                    tableObject.put("table", "searchhistory");
                    tableObject.put("data", dataJsonArray);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                bodyJson.put(tableObject);
                //End initial body json

                Log.d(TAG, "history json array: " + bodyJson);
                String url = Server.SEND_DATA_URL;
                RequestQueue requestQueue = Volley.newRequestQueue(context);
                JsonArrayRequest request = new JsonArrayRequest(Request.Method.POST, url, bodyJson, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(TAG, "onResponse: " + response);
                        if(response.length() > 0){
                            try {
                                JSONObject dataArray = (JSONObject) response.get(0);
                                JSONArray array = (JSONArray) dataArray.get("data");
                                int length = array.length();
                                for (int i = 0; i < length; i++) {
                                    JSONObject data = (JSONObject) array.get(i);
                                    int idServer = data.getInt("IdServer");
                                    int historyId = data.getInt("Id");
                                    databaseAccess.updateHistoryIdServer(historyId, idServer);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Error: ", error.getMessage());
                    }
                });
                requestQueue.add(request);
            }
        }else{
            Log.d(TAG, "onReceive: no login");
        }
    }
}
