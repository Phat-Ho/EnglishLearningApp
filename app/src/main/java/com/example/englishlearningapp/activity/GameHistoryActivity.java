package com.example.englishlearningapp.activity;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.englishlearningapp.R;
import com.example.englishlearningapp.adapters.GameHistoryAdapter;
import com.example.englishlearningapp.models.Game;
import com.example.englishlearningapp.models.HistoryGameWord;
import com.example.englishlearningapp.utils.DatabaseAccess;
import com.example.englishlearningapp.utils.GlobalVariable;

import java.util.ArrayList;

public class GameHistoryActivity extends AppCompatActivity {
    Toolbar historyToolbar;
    ListView historyListView;
    ArrayList<Game> gameList;
    DatabaseAccess databaseAccess;
    GameHistoryAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GlobalVariable.changeStatusBarColor(GameHistoryActivity.this);
        setContentView(R.layout.activity_history);
        databaseAccess = DatabaseAccess.getInstance(this);
        MappingView();
        SetUpToolbar();
        SetUpListView();
    }

    private void SetUpListView() {
        gameList = databaseAccess.getAllGame();
        adapter = new GameHistoryAdapter(this, gameList);
        historyListView.setAdapter(adapter);
        historyListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ArrayList<HistoryGameWord> wordList = databaseAccess.getGameDetailById(gameList.get(position).getId());
                Intent intent = new Intent(GameHistoryActivity.this, InGameHistoryActivity.class);
                intent.putExtra("gameHistoryWord", wordList);
                startActivity(intent);
            }
        });
    }

    private void SetUpToolbar() {
        historyToolbar.setTitle("");
        setSupportActionBar(historyToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            historyToolbar.getNavigationIcon().setColorFilter(getColor(android.R.color.white), PorterDuff.Mode.SRC_IN);
        }
        historyToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void MappingView() {
        historyToolbar = findViewById(R.id.history_toolbar);
        historyListView = findViewById(R.id.history_lv);
    }
}
