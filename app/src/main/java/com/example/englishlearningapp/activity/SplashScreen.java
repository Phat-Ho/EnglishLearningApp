package com.example.englishlearningapp.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.ContentLoadingProgressBar;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.englishlearningapp.R;
import com.example.englishlearningapp.receiver.NetworkChangeReceiver;
import com.example.englishlearningapp.utils.DatabaseAccess;
import com.example.englishlearningapp.utils.DatabaseContract;
import com.example.englishlearningapp.utils.GlobalVariable;
import com.example.englishlearningapp.utils.LoginManager;
import com.example.englishlearningapp.utils.Server;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SplashScreen extends AppCompatActivity {

    private static final String TAG = "SplashScreen";
    LoginManager loginManager;
    DatabaseAccess database;
    TextView txtSplash;
    ContentLoadingProgressBar progressBarSplash;
    NetworkChangeReceiver networkChangeReceiver;
    int delayTimes = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GlobalVariable.changeStatusBarColor(SplashScreen.this);
        setContentView(R.layout.activity_splash_screen);
        database = DatabaseAccess.getInstance(this);
        database.open();
        loginManager = new LoginManager(this);
        MappingView();
        if(loginManager.isLogin()){
            syncHistoryRemoteDbToLocalDb();
        }
        CloseSplashScreen();
    }

    private void CloseSplashScreen() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                Intent welcomeIntent = new Intent(SplashScreen.this, WelcomeActivity.class);
                startActivity(welcomeIntent);
                finish();
            }
        };

        Handler handler = new Handler();
        handler.postDelayed(runnable, delayTimes);
    }

    private void MappingView() {
        txtSplash = findViewById(R.id.splash_txt);
        progressBarSplash = findViewById(R.id.splash_progress_bar);
    }

    private void syncHistoryRemoteDbToLocalDb() {
        final int userId = loginManager.getUserId();
        String getHistoryUrl = Server.GET_HISTORY_URL + "userid=" + userId;
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest getHistoryRequest = new StringRequest(Request.Method.GET, getHistoryUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    if(jsonArray.length() > 0){
                        for(int i =0; i<jsonArray.length(); i++) {
                            JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                            int syncStatus = Integer.parseInt(jsonObject.get("syncStatus").toString());
                            if(syncStatus == DatabaseContract.NOT_SYNC){
                                int wordId = Integer.parseInt(jsonObject.get("wordId").toString());
                                String date = jsonObject.get("date").toString();
                                database.addHistory(wordId, dateTimeToMillis(date));
                                updateRemoteHistory(SplashScreen.this, userId, wordId);
                                Log.d(TAG, "onResponse: sync success form remote to local");
                            }
                        }
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
        requestQueue.add(getHistoryRequest);
    }

    private void updateRemoteHistory(Context context, int userId, int wordId){
        String updateHistoryUrl = Server.UPDATE_HISTORY_URL + "userid=" + userId + "&wordid=" + wordId;
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        StringRequest updateHistoryRequest = new StringRequest(Request.Method.GET, updateHistoryUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String message = (String) jsonObject.get("message");
                    if(message.equals("success")){
                        Log.d(TAG, "onResponse: sync success remote to local");
                    }else{
                        Log.d(TAG, "onResponse: sync fail remote to local");
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
        requestQueue.add(updateHistoryRequest);
    }

    @Override
    protected void onStart() {
        super.onStart();
        initNetworkChangeReceiver();
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(networkChangeReceiver);
    }

    private long dateTimeToMillis(String datetime){
        Date date = null;
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        try {
            date = dateFormatter.parse(datetime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if(date == null){
            return 0;
        }else{
            return date.getTime();
        }
    }

    private void initNetworkChangeReceiver() {
        networkChangeReceiver = new NetworkChangeReceiver();
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkChangeReceiver, intentFilter);
    }
}
