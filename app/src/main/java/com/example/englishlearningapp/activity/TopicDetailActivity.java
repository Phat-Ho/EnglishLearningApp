package com.example.englishlearningapp.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.englishlearningapp.R;
import com.example.englishlearningapp.models.Word;
import com.example.englishlearningapp.utils.DatabaseAccess;

import java.util.ArrayList;

public class TopicDetailActivity extends AppCompatActivity {
    Toolbar topicDetailToolbar;
    TextView topicNameTxt;
    ListView topicDetailListView;
    DatabaseAccess databaseAccess;
    ArrayAdapter adapter;
    ArrayList<Word> wordList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topic_detail);
        databaseAccess = DatabaseAccess.getInstance(this);
        SetUpView();
        SetUpToolbar();
        SetUpListView();
    }

    private void SetUpView() {
        topicDetailToolbar = findViewById(R.id.topic_detail_toolbar);
        topicDetailListView = findViewById(R.id.topic_detail_lv);
        topicNameTxt = findViewById(R.id.topic_mame_txt);
    }

    private void SetUpListView() {
        int topicId = getIntent().getIntExtra("topicId", 1);
        String topicName = getIntent().getStringExtra("topicName");
        topicNameTxt.setText(topicName);
        wordList = databaseAccess.getWordsByTopicId(topicId);
        adapter = new ArrayAdapter(this, android.R.layout.simple_expandable_list_item_1, wordList);
        topicDetailListView.setAdapter(adapter);

        topicDetailListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String html = wordList.get(position).getHtml();
                String word = wordList.get(position).getWord();
                int wordId = wordList.get(position).getId();
                int remembered = 0;
                moveToMeaningActivity(html, word, wordId, remembered);
            }
        });

    }

    private void SetUpToolbar() {
        setSupportActionBar(topicDetailToolbar);
        topicDetailToolbar.setTitle("");
        setSupportActionBar(topicDetailToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        topicDetailToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void moveToMeaningActivity(String html, String word, int wordId, int remembered) {
        Intent meaningIntent = new Intent(this, MeaningActivity.class);
        meaningIntent.putExtra("html", html);
        meaningIntent.putExtra("word", word);
        meaningIntent.putExtra("id", wordId);
        meaningIntent.putExtra("remembered", remembered);
        startActivity(meaningIntent);
    }
}