package com.example.englishlearningapp.activity;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.englishlearningapp.R;
import com.example.englishlearningapp.adapters.PlayerListRoomAdapter;
import com.example.englishlearningapp.models.Player;
import com.example.englishlearningapp.utils.DatabaseAccess;
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
    TextView roomInfoTitleTxt, roomInfoOwnerTxt, txtPlayerNum, txtPlayTime;
    MaterialButton btnStart;
    PlayerListRoomAdapter playerAdapter;
    ArrayList<Player> playerList = new ArrayList<>();
    GlobalVariable globalVariable;
    LoginManager loginManager;
    DatabaseAccess databaseAccess;
    boolean playerType;
    int roomId;
    int playerId;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_info);
        globalVariable = GlobalVariable.getInstance(this);
        GlobalVariable.changeStatusBarColor(this);
        loginManager = new LoginManager(this);
        databaseAccess = DatabaseAccess.getInstance(this);
        initView();
        GetIntentData();
        SetUpListView();
        SetUpToolbar();
        HandleStartGame();
        globalVariable.mSocket.on("sendRoomInfo", onSendRoom);
        globalVariable.mSocket.once("sendGame", onSendGame);
        globalVariable.mSocket.on("sendRoomRemove", onSendRoomRemove);
    }
    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: ");
        globalVariable.mSocket.off("sendRoomInfo", onSendRoom);
        globalVariable.mSocket.off("sendGame", onSendGame);
        globalVariable.mSocket.off("sendRoomRemove", onSendRoomRemove);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: ");
        globalVariable.mSocket.off("sendRoomInfo", onSendRoom);
        globalVariable.mSocket.off("sendGame", onSendGame);
        globalVariable.mSocket.off("sendRoomRemove", onSendRoomRemove);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
        globalVariable.mSocket.off("sendRoomInfo", onSendRoom);
        globalVariable.mSocket.off("sendGame", onSendGame);
        globalVariable.mSocket.off("sendRoomRemove", onSendRoomRemove);
        JSONObject jsonObj = new JSONObject();
        try {
            jsonObj.put("roomId", roomId);
            jsonObj.put("playerId", loginManager.getUserId());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        globalVariable.mSocket.emit("leaveRoom", jsonObj);
    }

    private void SetUpListView() {
        playerAdapter = new PlayerListRoomAdapter(this, playerList, roomId, globalVariable);
        listViewRoomInfo.setAdapter(playerAdapter);
    }

    private void GetIntentData() {
        Intent intent = getIntent();
        roomId = intent.getIntExtra("roomId", 0);
        String roomOwner = intent.getStringExtra("roomOwner");
        String roomName = intent.getStringExtra("roomName");
        int time = intent.getIntExtra("time", 10);
        int playerNum = intent.getIntExtra("playerNum", 2);
        roomInfoTitleTxt.setText(roomName);
        roomInfoOwnerTxt.setText(roomOwner);
        txtPlayerNum.setText(String.valueOf(playerNum));
        txtPlayTime.setText(String.valueOf(time));
        String playerName = intent.getStringExtra("playerName");
        playerId = loginManager.getUserId();

        if(intent.hasExtra("isPlay")){
            playerType = intent.getBooleanExtra("isPlay", true);
        }

        if(playerName != null){
            Player player = new Player(playerId, playerName, true);
            playerList.add(player);
        }
        ArrayList<Player> players = (ArrayList<Player>) intent.getSerializableExtra("playerList");
        if(players != null){
            playerList.addAll(players);
        }

        if(playerList.get(0).getId() == playerId){
            btnStart.setVisibility(View.VISIBLE);
        }else{
            btnStart.setVisibility(View.INVISIBLE);
        }
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

    private Emitter.Listener onSendRoomRemove = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, "send room info (remove): " + args[0].toString());
                    JSONObject object = (JSONObject) args[0];
                    try {
                        JSONObject roomObj = object.getJSONObject("newRoom");
                        int removedPlayerId = object.getInt("removedPlayer");
                        if(removedPlayerId == loginManager.getUserId()){
                            Toast.makeText(RoomInfoActivity.this, getResources().getString(R.string.kicked), Toast.LENGTH_SHORT).show();
                            finish();
                        }else{
                            JSONArray playerArray = roomObj.getJSONArray("players");
                            int length = playerArray.length();
                            if(length > 0){
                                ArrayList<Player> temp = new ArrayList<>();
                                for (int i = 0; i < length; i++)
                                {
                                    int id = playerArray.getJSONObject(i).getInt("playerId");
                                    String name = playerArray.getJSONObject(i).getString("playerName");
                                    boolean isPlay = playerArray.getJSONObject(i).getBoolean("isPlay");
                                    if(id == playerId){
                                        playerType = isPlay;
                                    }
                                    if(isPlay){
                                        Player player = new Player(id, name, true);
                                        temp.add(player);
                                    }
                                }

                                if(temp.get(0).getId() == loginManager.getUserId()){
                                    btnStart.setVisibility(View.VISIBLE);
                                }else{
                                    btnStart.setVisibility(View.INVISIBLE);
                                }
                                playerList.clear();
                                playerList.addAll(temp);
                                Log.d(TAG, "player List: " + playerList.toString());
                                playerAdapter.notifyDataSetChanged();
                            }
                            String playerNum = roomObj.getString("numOfPlayers");
                            txtPlayerNum.setText(playerNum);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };

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
                        int length = playerArray.length();
                        if(length > 0){
                            ArrayList<Player> temp = new ArrayList<>();
                            for (int i = 0; i < length; i++)
                            {
                                int id = playerArray.getJSONObject(i).getInt("playerId");
                                String name = playerArray.getJSONObject(i).getString("playerName");
                                boolean isPlay = playerArray.getJSONObject(i).getBoolean("isPlay");
                                if(id == playerId){
                                    playerType = isPlay;
                                }
                                if(isPlay){
                                    Player player = new Player(id, name, true);
                                    temp.add(player);
                                }
                            }

                            if(temp.get(0).getId() == loginManager.getUserId()){
                                btnStart.setVisibility(View.VISIBLE);
                            }else{
                                btnStart.setVisibility(View.INVISIBLE);
                            }
                            playerList.clear();
                            playerList.addAll(temp);
                            Log.d(TAG, "player List: " + playerList.toString());
                            playerAdapter.notifyDataSetChanged();
                        }
                        String playerNum = roomObj.getString("numOfPlayers");
                        txtPlayerNum.setText(playerNum);
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
                        int gameId = gameObj.getInt("id");
                        JSONObject joinGameObj = new JSONObject();
                        joinGameObj.put("gameId", gameId);
                        joinGameObj.put("roomId", roomId);
                        globalVariable.mSocket.emit("joinGame", joinGameObj);
                        String currentWord = gameObj.getString("currentWord");
                        JSONArray playersOrder = gameObj.getJSONArray("playerOrder");
                        ArrayList<Player> temp = new ArrayList<>();
                        int length = playersOrder.length();
                        if(length > 0) {
                            for (int i = 0; i < length; i++) {
                                int id = playersOrder.getJSONObject(i).getInt("playerId");
                                String name = playersOrder.getJSONObject(i).getString("playerName");
                                boolean isPlay = playersOrder.getJSONObject(i).getBoolean("isPlay");
                                if(isPlay){
                                    Player player = new Player(id, name, true);
                                    temp.add(player);
                                }
                            }
                        }
                        long date = gameObj.getLong("date");
                        String roomName = gameObj.getString("roomName");
                        long gameDBId = databaseAccess.addGame(date, roomName);
                        Intent gameIntent = new Intent(RoomInfoActivity.this, GameActivity.class);
                        gameIntent.putExtra("currentWord", currentWord);
                        gameIntent.putExtra("gameId", gameId);
                        gameIntent.putExtra("playerList", temp);
                        gameIntent.putExtra("gameDBId", gameDBId);
                        gameIntent.putExtra("isPlay", playerType);
                        globalVariable.mSocket.off("sendRoomInfo", onSendRoom);
                        globalVariable.mSocket.off("sendGame", onSendGame);
                        globalVariable.mSocket.off("sendRoomRemove", onSendRoomRemove);
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
        txtPlayTime = findViewById(R.id.txt_room_info_time);
        txtPlayerNum = findViewById(R.id.txt_room_info_number);
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