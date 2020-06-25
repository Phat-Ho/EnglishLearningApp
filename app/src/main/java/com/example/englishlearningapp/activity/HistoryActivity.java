package com.example.englishlearningapp.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.view.View;

import com.example.englishlearningapp.R;
import com.example.englishlearningapp.adapters.HistoryViewPagerAdapter;
import com.example.englishlearningapp.fragments.FavoriteFragment;
import com.example.englishlearningapp.fragments.HistoryFragment;
import com.example.englishlearningapp.fragments.RememberedFragment;
import com.example.englishlearningapp.utils.GlobalVariable;
import com.google.android.material.tabs.TabLayout;

public class HistoryActivity extends AppCompatActivity {
    Toolbar historyToolbar;
    TabLayout historyTabLayout;
    ViewPager historyViewPager;

    HistoryFragment historyFragment = new HistoryFragment();
    FavoriteFragment favoriteFragment = new FavoriteFragment();
    RememberedFragment rememberedFragment = new RememberedFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GlobalVariable.hideStatusBar(HistoryActivity.this);
        setContentView(R.layout.activity_history);
        MappingView();
        SetUpToolbar();
        SetUpViewPager(historyViewPager);
        historyTabLayout.setupWithViewPager(historyViewPager);
    }

    private void SetUpViewPager(ViewPager viewPager) {
        HistoryViewPagerAdapter pagerAdapter = new HistoryViewPagerAdapter(getSupportFragmentManager());
        pagerAdapter.addFragment(historyFragment, "Lịch sử");
        pagerAdapter.addFragment(favoriteFragment, "Yêu thích");
        pagerAdapter.addFragment(rememberedFragment, "Đã nhớ");
        viewPager.setAdapter(pagerAdapter);
    }

    private void SetUpToolbar() {
        setSupportActionBar(historyToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        historyToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void MappingView() {
        historyTabLayout = findViewById(R.id.history_tabLayout);
        historyToolbar = findViewById(R.id.history_toolbar);
        historyViewPager = findViewById(R.id.history_viewPager);
    }
}
