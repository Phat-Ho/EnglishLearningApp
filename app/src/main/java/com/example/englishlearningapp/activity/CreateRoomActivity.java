package com.example.englishlearningapp.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.englishlearningapp.R;
import com.example.englishlearningapp.utils.GlobalVariable;

public class CreateRoomActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GlobalVariable.changeStatusBarColor(this);
        setContentView(R.layout.activity_create_room);
    }
}