package com.example.englishlearningapp.activity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import com.example.englishlearningapp.R;
import com.example.englishlearningapp.utils.GlobalVariable;

public class VietnameseMeaningActivity extends AppCompatActivity {
    TextView vnMeaningTxt;
    Toolbar vnMeaningToolbar;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GlobalVariable.changeStatusBarColor(VietnameseMeaningActivity.this);
        setContentView(R.layout.activity_vietnamese_meaning);
        MappingView();
        SetUpToolbar();
        SetUpMeaning();
    }

    private void SetUpMeaning() {
        Intent intent = getIntent();
        String html = intent.getStringExtra("html");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            vnMeaningTxt.setText(Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY));
        } else {
            vnMeaningTxt.setText(Html.fromHtml(html));
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void SetUpToolbar() {
        vnMeaningToolbar.setTitle("");
        setSupportActionBar(vnMeaningToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        vnMeaningToolbar.getNavigationIcon().setColorFilter(getColor(android.R.color.white), PorterDuff.Mode.SRC_IN);
        vnMeaningToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void MappingView() {
        vnMeaningToolbar = findViewById(R.id.vn_meaning_toolbar);
        vnMeaningTxt = findViewById(R.id.vn_meaning_txt);
    }
}
