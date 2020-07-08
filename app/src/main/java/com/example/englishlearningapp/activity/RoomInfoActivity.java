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
import com.example.englishlearningapp.adapters.PlayerListRoomAdapter;
import com.example.englishlearningapp.models.Player;
import com.example.englishlearningapp.utils.GlobalVariable;
import com.example.englishlearningapp.utils.LoginManager;
import com.github.nkzawa.emitter.Emitter;
import com.google.android.material.button.MaterialButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class RoomInfoActivity extends AppCompatActivity {

    private static final String TAG = "RoomInfoActivity";
    Toolbar toolbarRoomInfo;
    ListView listViewRoomInfo;
    TextView roomInfoTitleTxt, roomInfoOwnerTxt;
    MaterialButton btnStart;
    PlayerListRoomAdapter playerAdapter;
    ArrayList<Player> playerList = new ArrayList<>();
    GlobalVariable globalVariable;
    LoginManager loginManager;
    int roomId;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_info);
        globalVariable = GlobalVariable.getInstance(this);
        loginManager = new LoginManager(this);
        GetIntentData();
        initView();
        SetUpListView();
        SetUpToolbar();
        HandleStartGame();
    }

    @Override
    protected void onStart() {
        super.onStart();
        globalVariable.mSocket.on("sendRoomInfo", onSendRoom);
        globalVariable.mSocket.once("sendGame", onSendGame);
    }

    @Override
    protected void onPause() {
        super.onPause();
        globalVariable.mSocket.off("sendRoomInfo", onSendRoom);
        globalVariable.mSocket.off("sendGame", onSendGame);
    }

    private void SetUpListView() {
        playerAdapter = new PlayerListRoomAdapter(this, playerList);
        listViewRoomInfo.setAdapter(playerAdapter);
    }

    private void GetIntentData() {
        Intent intent = getIntent();
        roomId = intent.getIntExtra("roomId", 0);
        String playerName = intent.getStringExtra("playerName");
        int playerId = loginManager.getUserId();
        if(playerName != null){
            Player player = new Player(playerId, playerName);
            playerList.add(player);
        }
        ArrayList<Player> players = (ArrayList<Player>) intent.getSerializableExtra("playerList");
        if(players != null){
            playerList.addAll(players);
        }
        Log.d(TAG, "GetIntentData: player List: " + playerList.toString());
    }

    private void HandleStartGame() {
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(playerList.size() > 1){
                    globalVariable.mSocket.emit("startGame", roomId);
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
                    Log.d(TAG, "send room info: " + args[0].toString());
                    JSONObject roomObj = (JSONObject) args[0];
                    try {
                        JSONArray playerArray = roomObj.getJSONArray("players");
                        String owner = roomObj.getString("owner");
                        roomInfoTitleTxt.setText(owner + "'s Room");
                        roomInfoOwnerTxt.setText(owner);
                        int length = playerArray.length();
                        if(length > 0){
                            ArrayList<Player> temp = new ArrayList<>();
                            for (int i = 0; i < length; i++)
                            {
                                int id = playerArray.getJSONObject(i).getInt("playerId");
                                String name = playerArray.getJSONObject(i).getString("playerName");
                                Player player = new Player(id, name);
                                temp.add(player);
                            }
                            playerList.clear();
                            playerList.addAll(temp);
                            Log.d(TAG, "player List: " + playerList.toString());
                            playerAdapter.notifyDataSetChanged();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };

    private Emitter.Listener onSendGame = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            Log.d(TAG, "args: " + args.toString());
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, "send game: " + args[0].toString());
                    JSONObject gameObj = (JSONObject) args[0];
                    try {
                        String currentWord = gameObj.getString("currentWord");
                        JSONArray playersOrder = gameObj.getJSONArray("playerOrder");
                        ArrayList<Player> temp = new ArrayList<>();
                        int length = playersOrder.length();
                        if(length > 0) {
                            for (int i = 0; i < length; i++) {
                                int id = playersOrder.getJSONObject(i).getInt("playerId");
                                String name = playersOrder.getJSONObject(i).getString("playerName");
                                Player player = new Player(id, name);
                                temp.add(player);
                            }
                        }
                        Intent gameIntent = new Intent(RoomInfoActivity.this, GameActivity.class);
                        gameIntent.putExtra("currentWord", currentWord);
                        gameIntent.putExtra("roomId", roomId);
                        gameIntent.putExtra("playerList", temp);
                        startActivity(gameIntent);
                        finish();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };

    private void initView(){
        btnStart = findViewById(R.id.buttonStartGame);
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