package com.example.englishlearningapp.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.englishlearningapp.R;
import com.example.englishlearningapp.adapters.RoomAdapter;
import com.example.englishlearningapp.models.Room;
import com.example.englishlearningapp.utils.GlobalVariable;
import com.example.englishlearningapp.utils.LoginManager;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Socket;
import com.google.android.material.button.MaterialButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class RoomListActivity extends AppCompatActivity {

    private static final String TAG = "RoomListActivity";
    ListView lvRoomList;
    ArrayList<Room> arrRoom;
    Toolbar toolbarRoomList;
    RoomAdapter adapter;
    Room room;
    LoginManager loginManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_list);
        initView();
        SetUpToolbar();
        loginManager = new LoginManager(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        GlobalVariable.mSocket.emit("getRoom");
        GlobalVariable.mSocket.on("roomList", onRetrieveRoomList);
        GlobalVariable.mSocket.on("sendRoomList", onRetrieveRoomList);
        GlobalVariable.mSocket.on("sendRoomInfo", onSendRoomInfo);
    }

    private Emitter.Listener onRetrieveRoomList = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    arrRoom.clear();
                    JSONObject jsonObject = (JSONObject) args[0];
                    try {
                        JSONArray roomData = jsonObject.getJSONArray("roomList");
                        for (int i = 0; i < roomData.length(); i++){
                            JSONObject object = roomData.getJSONObject(i);
                            int id = object.getInt("id");
                            String name = object.getString("name");
                            Integer numOfPlayers = object.getInt("numOfPlayers");
                            String password = object.getString("password");
                            String timer = object.getString("timer");
                            room = new Room(id, name, numOfPlayers, password, timer);
                            arrRoom.add(room);
                            adapter.notifyDataSetChanged();
                        }
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
                        int length = playerArray.length();
                        if(length > 0){
                            ArrayList<String> temp = new ArrayList<>();
                            for (int i = 0; i < length; i++) {
                                temp.add(playerArray.getString(i));
                            }
                            Intent roomInfoIntent = new Intent(RoomListActivity.this, RoomInfoActivity.class);
                            roomInfoIntent.putExtra("roomId", roomId);
                            roomInfoIntent.putStringArrayListExtra("players", temp);
                            startActivity(roomInfoIntent);
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
        arrRoom = new ArrayList<>();
        adapter = new RoomAdapter(this, R.layout.row_room_list_view, arrRoom);
        lvRoomList.setAdapter(adapter);
        lvRoomList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int roomId = arrRoom.get(position).getId();
                String playerName = loginManager.getUserName();
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("roomId", roomId);
                    jsonObject.put("playerName", playerName);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                GlobalVariable.mSocket.emit("joinRoom", jsonObject);
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