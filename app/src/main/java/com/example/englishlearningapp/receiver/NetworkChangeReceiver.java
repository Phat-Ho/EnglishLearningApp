package com.example.englishlearningapp.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.provider.ContactsContract;
import android.util.Log;
import android.widget.Toast;

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

public class NetworkChangeReceiver extends BroadcastReceiver {
    private static final String TAG = "NetworkChangeReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        if(ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())){
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
                            final int wordID = cursor.getInt(0);
                            final String dateTime = cursor.getString(1);
                            dateTime.replace(" ", "%20");
                            Log.d(TAG, "datetime: " + dateTime);
                            String url = Server.ADD_HISTORY_URL + "userid=0&wordid=" + wordID + "&datetime=" + dateTime;
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
                            });
                            requestQueue.add(stringRequest);
                        }
                    }while (cursor.moveToNext());
                }
            }
        }
    }
}
