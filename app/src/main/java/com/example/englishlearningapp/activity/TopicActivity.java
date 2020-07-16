package com.example.englishlearningapp.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.englishlearningapp.R;
import com.example.englishlearningapp.adapters.SubjectAdapter;
import com.example.englishlearningapp.models.Topic;
import com.example.englishlearningapp.utils.DatabaseAccess;

import java.util.ArrayList;

public class TopicActivity extends AppCompatActivity {
    private static final String TAG = "TopicActivity";
    RecyclerView topicRecyclerView;
    Toolbar topicToolbar;
    ArrayList<Topic> topicList;
    SubjectAdapter subjectAdapter;
    DatabaseAccess databaseAccess;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topic);
        databaseAccess = DatabaseAccess.getInstance(this);
        InitialView();
        SetUpToolBar();
        SetUpListView();
    }

    private void SetUpListView() {
        topicList = databaseAccess.getAllTopic();
        Log.d(TAG, "topic List size: " + topicList.size());
        //init Adapter
        subjectAdapter = new SubjectAdapter(topicList, this);
        topicRecyclerView.setHasFixedSize(true);

        //Set up layout
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        topicRecyclerView.setLayoutManager(layoutManager);

        //Attach adapter
        topicRecyclerView.setAdapter(subjectAdapter);
    }

    private void SetUpToolBar() {
        topicToolbar.setTitle("");
        setSupportActionBar(topicToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            topicToolbar.getNavigationIcon().setColorFilter(getColor(android.R.color.white), PorterDuff.Mode.SRC_IN);
        }
        topicToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void InitialView() {
        topicRecyclerView = findViewById(R.id.topic_recyclerview);
        topicToolbar = findViewById(R.id.toolbarTopic);
    }
}