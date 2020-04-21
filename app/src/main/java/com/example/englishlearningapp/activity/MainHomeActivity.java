package com.example.englishlearningapp.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.englishlearningapp.R;
import com.example.englishlearningapp.fragments.LoginFragment;
import com.example.englishlearningapp.fragments.SettingFragment;
import com.example.englishlearningapp.navigation_bottom_fragments.HistoryFavoriteFragment;
import com.example.englishlearningapp.navigation_bottom_fragments.HomeFragment;
import com.example.englishlearningapp.navigation_bottom_fragments.ProfileFragment;
import com.example.englishlearningapp.navigation_bottom_fragments.SearchFragment;
import com.example.englishlearningapp.utils.LoginManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainHomeActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigation;

    final Fragment homeFragment = new HomeFragment();
    final Fragment loginFragment = new LoginFragment();
    final Fragment settingFragment = new SettingFragment();
    final Fragment profileFragment = new ProfileFragment();
    final Fragment historyFavFragment = new HistoryFavoriteFragment();
    final FragmentManager fm = getSupportFragmentManager();
    Fragment activeFragment = homeFragment;
    private static final String TAG = "MainHomeActivity";
    LoginManager loginManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_home);
        loginManager = new LoginManager(MainHomeActivity.this);
        setHomeFragment();
        bottomNavigation = findViewById(R.id.navigation_bottom);
        bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.navigation_home:
                        showFragment(homeFragment);
                        return true;
                    case R.id.navigation_history:
                        showFragment(historyFavFragment);
                        return true;
                    case R.id.navigation_setting:
                        showFragment(settingFragment);
                        return true;
                    case R.id.navigation_profile:
                        if(loginManager.isLogin()){
                            showFragment(profileFragment);
                        }else{
                            showFragment(loginFragment);
                        }
                        return true;
                }
                return false;
            }
        });
    }

    private void setHomeFragment(){
        fm.beginTransaction().add(R.id.container, homeFragment, "home").commit();
    }

    public void showFragment(Fragment fragToShow){
        fm.beginTransaction().replace(R.id.container, fragToShow).commit();
        activeFragment = fragToShow;
    }

    @Override
    public void onBackPressed() {
        if(activeFragment == homeFragment){
            finish();
        }else{
            bottomNavigation.setSelectedItemId(R.id.navigation_home);
            activeFragment = homeFragment;
        }
    }

}
