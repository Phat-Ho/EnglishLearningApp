package com.example.englishlearningapp.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.englishlearningapp.R;
import com.example.englishlearningapp.fragments.SettingFragment;
import com.example.englishlearningapp.interfaces.MyListener;
import com.example.englishlearningapp.models.Word;
import com.example.englishlearningapp.receiver.AlarmReceiver;
import com.example.englishlearningapp.receiver.NetworkChangeReceiver;
import com.example.englishlearningapp.utils.DatabaseAccess;
import com.example.englishlearningapp.utils.DatabaseContract;
import com.example.englishlearningapp.utils.Server;
import com.google.android.material.button.MaterialButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends AppCompatActivity{

    private static final String TAG = "MainActivity";
    MaterialButton nextButton, btnLogin;
    Spinner spinnerLang;
    ImageView imgLogo;
    String[] languages = new String[]{"Tiếng Việt", "English"};
    AlarmManager alarmManager;
    NetworkChangeReceiver networkChangeReceiver;
    SharedPreferences loginPref;
    DatabaseAccess database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MappingView();
        database = DatabaseAccess.getInstance(this);
        database.open();
        loginPref = getSharedPreferences("loginState", MODE_PRIVATE);
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        imgLogo.setImageResource(R.mipmap.ic_launcher);
        ArrayAdapter adapter = new ArrayAdapter(MainActivity.this, android.R.layout.simple_list_item_1, languages);
        spinnerLang.setAdapter(adapter);

        spinnerLang.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0){
                    setLocale("vi");
                    btnLogin.setText("Đăng nhập");
                } else {
                    setLocale("en");
                    btnLogin.setText("Sign in");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MainHomeActivity.class);
                startActivity(intent);
                finish();
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(loginIntent);
                finish();
            }
        });

        if(loginPref.getBoolean("isLogin", false) == true){
            syncHistoryRemoteDbToLocalDb();
        }

    }

    private void syncHistoryRemoteDbToLocalDb() {
        final int userId = loginPref.getInt("userID", 0);
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
                                database.addHistory(wordId, DatabaseContract.SYNC, date);
                                updateRemoteHistory(MainActivity.this, userId, wordId);
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
        Log.d(TAG, "onStop:");
        unregisterReceiver(networkChangeReceiver);
    }

    private void initNetworkChangeReceiver() {
        networkChangeReceiver = new NetworkChangeReceiver();
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkChangeReceiver, intentFilter);
    }

    public void setLocale(String localeCode){
        Locale myLocale = new Locale(localeCode);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
    }

    private void MappingView() {
        nextButton = findViewById(R.id.home_next_button);
        spinnerLang = (Spinner) findViewById(R.id.spinner_language);
        imgLogo = (ImageView) findViewById(R.id.imageViewLogo);
        btnLogin = findViewById(R.id.home_login_button);
    }
}

