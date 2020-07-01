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


    private void initNetworkChangeReceiver() {
        networkChangeReceiver = new NetworkChangeReceiver();
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkChangeReceiver, intentFilter);
    }
}
