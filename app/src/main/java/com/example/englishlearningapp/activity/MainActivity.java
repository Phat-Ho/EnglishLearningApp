package com.example.englishlearningapp.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;

import com.example.englishlearningapp.R;
import com.example.englishlearningapp.models.Word;
import com.example.englishlearningapp.receiver.AlarmReceiver;
import com.example.englishlearningapp.utils.DatabaseAccess;
import com.google.android.material.button.MaterialButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    MaterialButton nextButton, btnLogin;
    Spinner spinnerLang;
    ImageView imgLogo;
    String[] languages = new String[]{"Tiếng Việt", "English"};
    AlarmManager alarmManager;
    DatabaseAccess db;
    SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MappingView();
        db = DatabaseAccess.getInstance(MainActivity.this);
        prefs = getSharedPreferences("historyIndex", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("index", 0);
        editor.apply();
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

        //Gọi hàm thông báo lặp lại mỗi 10 giây
        long timeInMillis = 1000; //1 second
        setRepeatAlarm(timeInMillis);
    }

    private void setRepeatAlarm(long timeInMillis) {
        if(db.getHistoryWords().size() > 0){
            int arrayIndex = prefs.getInt("index", 0);
            int id = 0;
            if(AlarmReceiver.historyWords == null){
                db.open();
                ArrayList<Word> historyWord = db.getHistoryWords();
                id = historyWord.get(arrayIndex).getId();
            }else{
                id = AlarmReceiver.historyWords.get(arrayIndex).getId();
            }

            Intent receiverIntent = new Intent(getApplicationContext(), AlarmReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this, id, receiverIntent, PendingIntent.FLAG_UPDATE_CURRENT);

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
