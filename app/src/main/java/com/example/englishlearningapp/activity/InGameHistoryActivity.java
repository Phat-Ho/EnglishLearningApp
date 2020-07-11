package com.example.englishlearningapp.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.englishlearningapp.R;
import com.example.englishlearningapp.adapters.HistoryGameAdapter;
import com.example.englishlearningapp.models.HistoryGameWord;

import java.util.ArrayList;

public class InGameHistoryActivity extends AppCompatActivity {
    Toolbar historyGameToolbar;
    ListView historyGameListView;
    HistoryGameAdapter adapter;
    ArrayList<HistoryGameWord> wordList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_history);
        SetUpView();
        SetUpToolbar();
        SetUpListView();
    }

    private void SetUpView() {
        historyGameToolbar = findViewById(R.id.game_history_toolbar);
        historyGameListView = findViewById(R.id.game_history_lv);
    }

    private void SetUpListView() {
        ArrayList<HistoryGameWord> tempList = (ArrayList<HistoryGameWord>) getIntent().getSerializableExtra("gameHistoryWord");
        wordList.addAll(tempList);
        adapter = new HistoryGameAdapter(this, wordList);
        historyGameListView.setAdapter(adapter);
    }

    private void SetUpToolbar() {
        setSupportActionBar(historyGameToolbar);
        historyGameToolbar.setTitle("");
        setSupportActionBar(historyGameToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        historyGameToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}