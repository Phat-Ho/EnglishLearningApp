package com.example.englishlearningapp.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.provider.ContactsContract;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.englishlearningapp.utils.DatabaseAccess;
import com.example.englishlearningapp.utils.DatabaseContract;
import com.example.englishlearningapp.utils.Server;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class NetworkChangeReceiver extends BroadcastReceiver {
    private static final String TAG = "NetworkChangeReceiver";
    SharedPreferences loginPref;
    @Override
    public void onReceive(Context context, Intent intent) {
        loginPref = context.getSharedPreferences("loginState",Context.MODE_PRIVATE);
        boolean isLogin = loginPref.getBoolean("isLogin", false);
        final int userID = loginPref.getInt("userID", 0);
        Log.d(TAG, "isLogin: " + isLogin);
        if(ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction()) && isLogin == true){
            boolean noConnectivity = intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);
            if(noConnectivity){
                Log.d(TAG, "onReceive: Disconnected");
            }else{
                Log.d(TAG, "onReceive: Connected");
                final DatabaseAccess databaseAccess = DatabaseAccess.getInstance(context);
                databaseAccess.open();
                //Get the history cursor from local database
                Cursor cursor = databaseAccess.readHistory();
                if(cursor.moveToFirst()){
                    do{
                        int syncStatus = cursor.getInt(cursor.getColumnIndex(DatabaseContract.SYNC_STATUS));
                        if(syncStatus == DatabaseContract.NOT_SYNC){ //Sync if the history word is not saved to server
                            final int wordID = cursor.getInt(cursor.getColumnIndex(DatabaseContract.WORD_ID));
                            final String dateTime = cursor.getString(cursor.getColumnIndex(DatabaseContract.DATE));
                            Log.d(TAG, "datetime: " + dateTime);
                            String url = Server.ADD_HISTORY_URL;
                            RequestQueue requestQueue = Volley.newRequestQueue(context);
                            StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    try {
                                        JSONObject jsonObject = new JSONObject(response);
                                        String message = jsonObject.getString("message");
                                        if(message.equals("success")){
                                            //Update the sync status of history word
                                            databaseAccess.updateHistorySyncStatus(wordID, DatabaseContract.SYNC);
                                            Log.d(TAG, "onResponse: Sync success");
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Log.d(TAG, "onErrorResponse: " + error.getMessage());
                                }
                            }){
                                @Override
                                protected Map<String, String> getParams() throws AuthFailureError {
                                    HashMap<String, String> params = new HashMap<>();
                                    params.put("userid", String.valueOf(userID));
                                    params.put("wordid", String.valueOf(wordID));
                                    params.put("datetime", dateTime);
                                    params.put("sync", String.valueOf(DatabaseContract.SYNC));

                                    return params;
                                }
                            };
                            requestQueue.add(stringRequest);
                        }
                    }while (cursor.moveToNext());
                }
            }
        }else{
            Log.d(TAG, "onReceive: no login");
        }
    }
}
