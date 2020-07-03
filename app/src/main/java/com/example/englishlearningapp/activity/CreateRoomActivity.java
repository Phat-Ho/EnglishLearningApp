package com.example.englishlearningapp.activity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;

import com.example.englishlearningapp.R;
import com.example.englishlearningapp.adapters.ConnectedWordAdapter;
import com.example.englishlearningapp.utils.GlobalVariable;

public class CreateRoomActivity extends AppCompatActivity {

    Toolbar createRoomToolbar;
    Spinner spinnerNumOfPlayers, spinnerDifficulty;
    EditText edtTimer, edtPasswordConnectedWord, edtRoomName;
    Switch swtPasswordConnectedWord;
    Button btnCreateRoom;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GlobalVariable.changeStatusBarColor(this);
        setContentView(R.layout.activity_create_room);
        initView();
        SetUpToolbar();
        onClickButton();
    }

    private void initView(){
        createRoomToolbar = findViewById(R.id.toolbarCreateRoom);
        spinnerNumOfPlayers = findViewById(R.id.spinnerNumberOfPlayers);
        spinnerDifficulty = findViewById(R.id.spinnerDifficulty);
        edtTimer = findViewById(R.id.editTextTimer);
        edtPasswordConnectedWord = findViewById(R.id.editTextPasswordConnectedWord);
        edtRoomName = findViewById(R.id.editTextRoomName);
        swtPasswordConnectedWord = findViewById(R.id.switchPasswordConnectedWord);
        btnCreateRoom = findViewById(R.id.buttonCreateRoom);
    }

    private void onClickButton(){
        btnCreateRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CreateRoomActivity.this, RoomInfoActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void SetUpToolbar() {
        createRoomToolbar.setTitle("");
        setSupportActionBar(createRoomToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        createRoomToolbar.getNavigationIcon().setColorFilter(getColor(android.R.color.white), PorterDuff.Mode.SRC_IN);
        createRoomToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


}