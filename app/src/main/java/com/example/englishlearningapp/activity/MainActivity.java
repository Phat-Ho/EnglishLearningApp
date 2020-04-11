package com.example.englishlearningapp.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.PendingIntent;
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

import com.example.englishlearningapp.R;
import com.example.englishlearningapp.fragments.SettingFragment;
import com.example.englishlearningapp.interfaces.MyListener;
import com.example.englishlearningapp.models.Word;
import com.example.englishlearningapp.receiver.AlarmReceiver;
import com.example.englishlearningapp.receiver.NetworkChangeReceiver;
import com.example.englishlearningapp.utils.DatabaseAccess;
import com.google.android.material.button.MaterialButton;

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
    DatabaseAccess db;
    SharedPreferences prefs, prefsNotify;
    NetworkChangeReceiver networkChangeReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MappingView();
        db = DatabaseAccess.getInstance(MainActivity.this);
        prefsNotify = getSharedPreferences("switch", MODE_PRIVATE);
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
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
//        initNetworkChangeReceiver();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop:");
//        unregisterReceiver(networkChangeReceiver);
    }

    private void initNetworkChangeReceiver() {
        networkChangeReceiver = new NetworkChangeReceiver();
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkChangeReceiver, intentFilter);
    }

    public void setRepeatAlarm(long timeInMillis) {
        db.open();
        if(db.getHistoryWords().size() > 0){
            Intent receiverIntent = new Intent(getApplicationContext(), AlarmReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0, receiverIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, calendar.getTime().getHours());
            calendar.set(Calendar.MINUTE, calendar.getTime().getMinutes());
            calendar.set(Calendar.SECOND, calendar.getTime().getSeconds());
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), timeInMillis, pendingIntent);
        }else{
            return;
        }

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

