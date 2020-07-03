package com.example.englishlearningapp.activity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import com.example.englishlearningapp.R;

public class RoomInfoActivity extends AppCompatActivity {

    Toolbar toolbarRoomInfo;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_info);
        initView();
        SetUpToolbar();
    }

    private void initView(){
        toolbarRoomInfo = findViewById(R.id.toolbarRoomInfo);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void SetUpToolbar() {
        toolbarRoomInfo.setTitle("");
        setSupportActionBar(toolbarRoomInfo);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbarRoomInfo.getNavigationIcon().setColorFilter(getColor(android.R.color.white), PorterDuff.Mode.SRC_IN);
        toolbarRoomInfo.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}