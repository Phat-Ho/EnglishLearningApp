package com.example.englishlearningapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentFactory;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.view.MenuItem;

import com.example.englishlearningapp.navigation_bottom_fragments.FriendsFragment;
import com.example.englishlearningapp.navigation_bottom_fragments.HomeFragment;
import com.example.englishlearningapp.navigation_bottom_fragments.ProfileFragment;
import com.example.englishlearningapp.navigation_bottom_fragments.SearchFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainHomeActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigation;

    final Fragment homeFragment = new HomeFragment();
    final Fragment searchFragment = new SearchFragment();
    final Fragment friendsFragment = new FriendsFragment();
    final Fragment profileFragment = new ProfileFragment();
    final FragmentManager fm = getSupportFragmentManager();
    Fragment activeFragment = homeFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_home);

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
                    case R.id.navigation_friends:
                        showFragment(friendsFragment);
                        return true;
                    case R.id.navigation_profile:
                        showFragment(profileFragment);
                        return true;
                }
                return false;
            }
        });
        setHomeFragment();
    }

    @Override
    public void onBackPressed() {
        if(activeFragment == homeFragment){
            finish();
        }else {
            activeFragment = homeFragment;
            fm.beginTransaction().replace(R.id.container, homeFragment).commit();
            bottomNavigation.setSelectedItemId(R.id.navigation_home);
        }
    }

    private void setHomeFragment(){
        fm.beginTransaction().add(R.id.container, homeFragment, "home").commit();
    }

    public void showFragment(Fragment fragToShow){
        fm.beginTransaction().replace(R.id.container, fragToShow).commit();
        activeFragment = fragToShow;
    }
}
