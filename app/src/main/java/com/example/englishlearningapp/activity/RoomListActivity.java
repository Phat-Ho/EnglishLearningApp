package com.example.englishlearningapp.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.englishlearningapp.R;
import com.example.englishlearningapp.adapters.RoomAdapter;
import com.example.englishlearningapp.models.Room;
import com.example.englishlearningapp.utils.GlobalVariable;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class RoomListActivity extends AppCompatActivity {

    ListView lvRoomList;
    ArrayList<Room> arrRoom;
    RoomAdapter adapter;
    Room room;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_list);
        initView();
        GlobalVariable.mSocket.connect();
        GlobalVariable.mSocket.emit("getRoom");
        GlobalVariable.mSocket.on("roomList", onRetrieveRoomList);
        GlobalVariable.mSocket.on("sendRoomList", onRetrieveRoomList);
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
                        JSONArray roomData = jsonObject.getJSONArray("room");
                        for (int i = 0; i < roomData.length(); i++){
                            JSONObject object = roomData.getJSONObject(i);
                            String name = object.getString("name");
                            Integer numOfPlayers = object.getInt("numOfPlayers");
                            String password = object.getString("password");
                            String timer = object.getString("timer");
                            room = new Room(name, numOfPlayers, password, timer);
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

    private void initView(){
        lvRoomList = findViewById(R.id.listViewRoomList);
        arrRoom = new ArrayList<>();
        adapter = new RoomAdapter(this, R.layout.row_room_list_view, arrRoom);
        lvRoomList.setAdapter(adapter);
    }

}