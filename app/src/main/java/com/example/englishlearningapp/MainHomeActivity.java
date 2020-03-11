package com.example.englishlearningapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
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
                        fm.beginTransaction().hide(activeFragment).show(homeFragment).commit();
                        activeFragment = homeFragment;
                        return true;
                    case R.id.navigation_search:
                        fm.beginTransaction().hide(activeFragment).show(searchFragment).commit();
                        activeFragment = searchFragment;
                        return true;
                    case R.id.navigation_friends:
                        fm.beginTransaction().hide(activeFragment).show(friendsFragment).commit();
                        activeFragment = friendsFragment;
                        return true;
                    case R.id.navigation_profile:
                        fm.beginTransaction().hide(activeFragment).show(profileFragment).commit();
                        activeFragment = profileFragment;
                        return true;
                }
                return false;
            }
        });
        setActiveFragment();
    }

    private void setActiveFragment(){
        fm.beginTransaction().add(R.id.container, profileFragment, "profile").hide(profileFragment).commit();
        fm.beginTransaction().add(R.id.container, friendsFragment, "friends").hide(friendsFragment).commit();
        fm.beginTransaction().add(R.id.container, searchFragment, "search").hide(searchFragment).commit();
        fm.beginTransaction().add(R.id.container, homeFragment, "home").commit();
    }
}
