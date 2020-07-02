package com.example.englishlearningapp.activity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.example.englishlearningapp.R;
import com.example.englishlearningapp.adapters.ConnectedWordAdapter;

import java.util.ArrayList;

public class ConnetedWordActivity extends AppCompatActivity {

    ListView lvConnectedWord;
    ConnectedWordAdapter adapter;
    ArrayList<Integer> imgResource;
    Toolbar connectedWordToolbar;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conneted_word);
        initView();
        SetUpToolbar();
    }

    private void initView(){
        lvConnectedWord = findViewById(R.id.listViewConnectedWord);
        connectedWordToolbar = findViewById(R.id.toolbarConnectedWord);
        imgResource = new ArrayList<>();
        imgResource.add(R.drawable.home_ic_4);
        adapter = new ConnectedWordAdapter(this, R.layout.row_connected_word, imgResource);
        lvConnectedWord.setAdapter(adapter);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void SetUpToolbar() {
        connectedWordToolbar.setTitle("");
        setSupportActionBar(connectedWordToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        connectedWordToolbar.getNavigationIcon().setColorFilter(getColor(android.R.color.white), PorterDuff.Mode.SRC_IN);
        connectedWordToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}