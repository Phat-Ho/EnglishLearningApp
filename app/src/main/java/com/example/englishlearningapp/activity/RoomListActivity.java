package com.example.englishlearningapp.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.englishlearningapp.R;
import com.example.englishlearningapp.adapters.RoomAdapter;
import com.example.englishlearningapp.models.Player;
import com.example.englishlearningapp.models.Room;
import com.example.englishlearningapp.utils.GlobalVariable;
import com.example.englishlearningapp.utils.LoginManager;
import com.github.nkzawa.emitter.Emitter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class RoomListActivity extends AppCompatActivity {

    private static final String TAG = "RoomListActivity";
    ListView lvRoomList;
    ArrayList<Room> arrRoom = new ArrayList<>();
    Toolbar toolbarRoomList;
    RoomAdapter adapter;
    LoginManager loginManager;
    GlobalVariable globalVariable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_list);
        globalVariable = GlobalVariable.getInstance(this);
        initView();
        SetUpToolbar();
        loginManager = new LoginManager(this);
        globalVariable.mSocket.emit("getRoom");
    }

    @Override
    protected void onStart() {
        super.onStart();
        globalVariable.mSocket.on("roomList", onRetrieveRoomList);
        globalVariable.mSocket.on("sendRoomInfo", onSendRoomInfo);
    }

    @Override
    protected void onPause() {
        super.onPause();
        globalVariable.mSocket.off("roomList", onRetrieveRoomList);
        globalVariable.mSocket.off("sendRoomInfo", onSendRoomInfo);
    }

    private Emitter.Listener onRetrieveRoomList = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, "on room list: " + args[0].toString());
                    JSONObject jsonObject = (JSONObject) args[0];
                    try {
                        JSONArray roomData = jsonObject.getJSONArray("activeRoom");
                        ArrayList<Room> temp = new ArrayList<>();
                        for (int i = 0; i < roomData.length(); i++){
                            JSONObject object = roomData.getJSONObject(i);
                            int id = object.getInt("id");
                            String name = object.getString("name");
                            Integer numOfPlayers = object.getInt("numOfPlayers");
                            String password = object.getString("password");
                            String time = object.getString("time");
                            JSONArray playerArr = object.getJSONArray("players");
                            Room room = new Room(id, name, numOfPlayers, password, time, playerArr.length());
                            temp.add(room);
                        }
                        Log.d(TAG, "temp room: " + temp.toString());
                        arrRoom.clear();
                        arrRoom.addAll(temp);
                        adapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };

    private Emitter.Listener onSendRoomInfo = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, "send room info: " + args[0].toString());
                    JSONObject roomObj = (JSONObject) args[0];
                    try {
                        JSONArray playerArray = roomObj.getJSONArray("players");
                        int roomId = roomObj.getInt("id");
                        String roomOwner = roomObj.getString("owner");
                        String roomName = roomObj.getString("name");
                        int length = playerArray.length();
                        if(length > 0){
                            ArrayList<Player> temp = new ArrayList<>();
                            for (int i = 0; i < length; i++) {
                                int id = playerArray.getJSONObject(i).getInt("playerId");
                                String name = playerArray.getJSONObject(i).getString("playerName");
                                Player player = new Player(id, name);
                                temp.add(player);
                            }
                            Intent roomInfoIntent = new Intent(RoomListActivity.this, RoomInfoActivity.class);
                            roomInfoIntent.putExtra("roomId", roomId);
                            roomInfoIntent.putExtra("playerList", temp);
                            roomInfoIntent.putExtra("roomOwner", roomOwner);
                            roomInfoIntent.putExtra("roomName", roomName);
                            startActivity(roomInfoIntent);
                            finish();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };

    private void initView(){
        lvRoomList = findViewById(R.id.listViewRoomList);
        toolbarRoomList = findViewById(R.id.toolbarRoomList);
        adapter = new RoomAdapter(this, R.layout.row_room_list_view, arrRoom);
        lvRoomList.setAdapter(adapter);
        lvRoomList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(arrRoom.get(position).getPlayerCount() >= arrRoom.get(position).getNumOfPlayers()){
                    Toast.makeText(RoomListActivity.this, "Room is full", Toast.LENGTH_SHORT).show();
                }else{
                    int roomId = arrRoom.get(position).getId();
                    String playerName = loginManager.getUserName();
                    JSONObject jsonObject = new JSONObject();
                    JSONObject playerObj = new JSONObject();
                    try {
                        jsonObject.put("roomId", roomId);
                        playerObj.put("playerId", loginManager.getUserId());
                        playerObj.put("playerName", playerName);
                        jsonObject.put("player", playerObj);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    globalVariable.mSocket.emit("joinRoom", jsonObject);
                }
            }
        });
    }

    private void SetUpToolbar() {
        toolbarRoomList.setTitle("");
        setSupportActionBar(toolbarRoomList);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbarRoomList.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

}