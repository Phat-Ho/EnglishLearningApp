package com.example.englishlearningapp.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentFactory;
import androidx.fragment.app.FragmentManager;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.example.englishlearningapp.R;
import com.example.englishlearningapp.fragments.SettingFragment;
import com.example.englishlearningapp.navigation_bottom_fragments.FriendsFragment;
import com.example.englishlearningapp.navigation_bottom_fragments.HomeFragment;
import com.example.englishlearningapp.navigation_bottom_fragments.ProfileFragment;
import com.example.englishlearningapp.navigation_bottom_fragments.SearchFragment;
import com.example.englishlearningapp.receiver.AlarmReceiver;
import com.example.englishlearningapp.utils.DatabaseAccess;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Calendar;

public class MainHomeActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigation;

    final Fragment homeFragment = new HomeFragment();
    final Fragment searchFragment = new SearchFragment();
    //final Fragment friendsFragment = new FriendsFragment();
    final Fragment settingFragment = new SettingFragment();
    final Fragment profileFragment = new ProfileFragment();
    final FragmentManager fm = getSupportFragmentManager();
    Fragment activeFragment = searchFragment;
    DatabaseAccess db;
    SharedPreferences prefs, prefsNotify;
    AlarmManager alarmManager;
    private static final String TAG = "MainHomeActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_home);

        /*db = DatabaseAccess.getInstance(MainHomeActivity.this);
        prefs = getSharedPreferences("historyIndex", MODE_PRIVATE);
        prefsNotify = getSharedPreferences("notify", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("index", 0);
        editor.apply();
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);*/

        setHomeFragment();

        bottomNavigation = findViewById(R.id.navigation_bottom);
        bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.navigation_home:
                        showFragment(homeFragment);
                        return true;
                    case R.id.navigation_search:
                        showFragment(searchFragment);
                        return true;
                    case R.id.navigation_setting:
                        showFragment(settingFragment);
                        return true;
                    case R.id.navigation_profile:
                        showFragment(profileFragment);
                        return true;
                }
                return false;
            }
        });
    }

    private void setHomeFragment(){
        fm.beginTransaction().add(R.id.container, searchFragment, "search").commit();
    }

    public void showFragment(Fragment fragToShow){
        fm.beginTransaction().replace(R.id.container, fragToShow).commit();
        activeFragment = fragToShow;
    }

    @Override
    public void onBackPressed() {
        if(activeFragment == searchFragment){
            finish();
        }else{
            bottomNavigation.setSelectedItemId(R.id.navigation_search);
            activeFragment = searchFragment;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        /*Log.d("AAA", "onStop");
        boolean isChecked = prefsNotify.getBoolean("checked", true);
        Log.d(TAG, "isChecked: " + isChecked);
        if (isChecked) {
            long timeInMillis = 1000; //1 second
            setRepeatAlarm(timeInMillis);
        }*/
    }

    public void setRepeatAlarm(long timeInMillis) {
        db.open();
        if(db.getHistoryWords().size() > 0){
            Intent receiverIntent = new Intent(getApplicationContext(), AlarmReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(MainHomeActivity.this, 0, receiverIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, 16);
            calendar.set(Calendar.MINUTE, 43);
            calendar.set(Calendar.SECOND, 0);
            //alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), timeInMillis, pendingIntent);
        }else{
            return;
        }

    }
}
