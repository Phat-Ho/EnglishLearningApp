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
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.englishlearningapp.R;
import com.example.englishlearningapp.utils.GlobalVariable;
import com.github.nkzawa.emitter.Emitter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class RoomInfoActivity extends AppCompatActivity {

    private static final String TAG = "RoomInfoActivity";
    Toolbar toolbarRoomInfo;
    ListView listViewRoomInfo;
    TextView roomInfoTitleTxt, roomInfoOwnerTxt;
    ArrayAdapter playerAdapter;
    ArrayList<String> playerList = new ArrayList<>();

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_info);
        initView();
        SetUpToolbar();
        SetUpListView();
        GlobalVariable.mSocket.on("sendRoomInfo", onSendRoom);
    }



    private Emitter.Listener onSendRoom = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, "send room: " + args[0].toString());
                    JSONObject roomObj = (JSONObject) args[0];
                    try {
                        JSONArray playerArray = roomObj.getJSONArray("players");
                        String owner = roomObj.getString("owner");
                        roomInfoTitleTxt.setText(owner + "'s Room");
                        roomInfoOwnerTxt.setText(owner);
                        int length = playerArray.length();
                        if(length > 0 && playerArray != null){
                            playerList.clear();
                            ArrayList<String> temp = new ArrayList<>();
                            for (int i = 0; i < length; i++) {
                                temp.add(playerArray.getString(i));
                            }
                            playerList.addAll(temp);
                            playerAdapter = new ArrayAdapter(RoomInfoActivity.this, android.R.layout.simple_list_item_1, playerList);
                            listViewRoomInfo.setAdapter(playerAdapter);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };

    private void SetUpListView() {
        Intent intent = getIntent();
        int roomId = intent.getIntExtra("roomId", 0);
        Log.d(TAG, "Room id: " + roomId);
        GlobalVariable.mSocket.emit("getRoomById", roomId);
    }

    private void initView(){
        toolbarRoomInfo = findViewById(R.id.toolbarRoomInfo);
        listViewRoomInfo = findViewById(R.id.listViewPlayer);
        roomInfoTitleTxt = findViewById(R.id.room_info_title_txt);
        roomInfoOwnerTxt = findViewById(R.id.txt_room_info_owner);
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