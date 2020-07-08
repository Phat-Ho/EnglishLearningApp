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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.example.englishlearningapp.R;
import com.example.englishlearningapp.adapters.ConnectedWordAdapter;
import com.example.englishlearningapp.models.Room;
import com.example.englishlearningapp.utils.GlobalVariable;
import com.example.englishlearningapp.utils.LoginManager;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;

public class CreateRoomActivity extends AppCompatActivity {

    private static final String TAG = "CreateRoomActivity";
    Toolbar createRoomToolbar;
    Spinner spinnerNumOfPlayers, spinnerDifficulty;
    EditText edtTimer, edtPasswordConnectedWord, edtRoomName;
    Switch swtPasswordConnectedWord;
    Button btnCreateRoom;
    String[] numOfPlayers = {"2", "3", "4", "5"};
    LoginManager loginManager;
    GlobalVariable globalVariable;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        globalVariable = GlobalVariable.getInstance(this);
        GlobalVariable.changeStatusBarColor(this);
        setContentView(R.layout.activity_create_room);
        initView();
        loginManager = new LoginManager(this);
        SetUpToolbar();
        onClickButton();
        handleSpinner();
        handleSwitch();

    }

    @Override
    protected void onStart() {
        super.onStart();
        globalVariable.mSocket.once("sendRoomOwner", onSendRoom);
    }

    private void initView(){
        createRoomToolbar = findViewById(R.id.toolbarCreateRoom);
        spinnerNumOfPlayers = findViewById(R.id.spinnerNumberOfPlayers);
        edtTimer = findViewById(R.id.editTextTimer);
        edtPasswordConnectedWord = findViewById(R.id.editTextPasswordConnectedWord);
        edtRoomName = findViewById(R.id.editTextRoomName);
        swtPasswordConnectedWord = findViewById(R.id.switchPasswordConnectedWord);
        btnCreateRoom = findViewById(R.id.buttonCreateRoom);
        edtPasswordConnectedWord.setVisibility(View.INVISIBLE);
    }

    private void onClickButton(){
        final String username = loginManager.getUserName();
        btnCreateRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String roomName = edtRoomName.getText().toString();
                Integer numOfPlayers = Integer.parseInt(spinnerNumOfPlayers.getSelectedItem().toString());
                String password = edtPasswordConnectedWord.getText().toString().trim();
                String timer = edtTimer.getText().toString().trim();
                if (roomName.equals("")){
                    Toast.makeText(CreateRoomActivity.this, "Vui lòng nhập tên phòng", Toast.LENGTH_SHORT).show();
                } else if (timer.equals("")){
                    Toast.makeText(CreateRoomActivity.this, "Vui lòng nhập thời gian", Toast.LENGTH_SHORT).show();
                } else {
                    JSONObject roomObject = new JSONObject();
                    try {
                        JSONArray playerList = new JSONArray();
                        playerList.put(username);
                        roomObject.put("owner", username);
                        roomObject.put("name", roomName);
                        roomObject.put("numOfPlayers", numOfPlayers);
                        roomObject.put("password", password);
                        roomObject.put("timer", timer);
                        roomObject.put("players", playerList);
                        globalVariable.mSocket.emit("createRoom", roomObject);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private Emitter.Listener onSendRoom = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, "send room owner: " + args[0].toString());
                    JSONObject roomObj = (JSONObject) args[0];
                    try {
                        int roomId = roomObj.getInt("id");
                        Intent roomInfoIntent = new Intent(CreateRoomActivity.this, RoomInfoActivity.class);
                        roomInfoIntent.putExtra("roomId", roomId);
                        roomInfoIntent.putExtra("name", loginManager.getUserName());
                        startActivity(roomInfoIntent);
                        finish();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };

    private void handleSwitch(){
        swtPasswordConnectedWord.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    edtPasswordConnectedWord.setVisibility(View.VISIBLE);
                } else {
                    edtPasswordConnectedWord.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    private void handleSpinner(){
        ArrayAdapter adapter = new ArrayAdapter(CreateRoomActivity.this, android.R.layout.simple_list_item_1, numOfPlayers);
        spinnerNumOfPlayers.setAdapter(adapter);
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