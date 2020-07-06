package com.example.englishlearningapp.activity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.englishlearningapp.R;
import com.example.englishlearningapp.adapters.ConnectedWordAdapter;
import com.example.englishlearningapp.utils.GlobalVariable;
import com.github.nkzawa.emitter.Emitter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
        GlobalVariable.changeStatusBarColor(this);
        setContentView(R.layout.activity_conneted_word);
        initView();
        SetUpToolbar();
    }

    @Override
    protected void onStart() {
        super.onStart();
        GlobalVariable.mSocket.connect();
    }

    private void initView(){
        lvConnectedWord = findViewById(R.id.listViewConnectedWord);
        connectedWordToolbar = findViewById(R.id.toolbarConnectedWord);
        imgResource = new ArrayList<>();
        imgResource.add(R.drawable.ic_logo);
        imgResource.add(R.drawable.ic_logo);
        adapter = new ConnectedWordAdapter(this, R.layout.row_connected_word, imgResource);
        lvConnectedWord.setAdapter(adapter);
        GlobalVariable.mSocket.connect();
        lvConnectedWord.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:
                        Intent createRoomIntent = new Intent(ConnetedWordActivity.this, CreateRoomActivity.class);
                        startActivity(createRoomIntent); break;
                    case 1:
                        Intent roomListIntent = new Intent(ConnetedWordActivity.this, RoomListActivity.class);
                        startActivity(roomListIntent); break;
                }
            }
        });
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