package com.example.englishlearningapp.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.englishlearningapp.R;
import com.example.englishlearningapp.utils.GlobalVariable;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class RoomListActivity extends AppCompatActivity {

    ListView lvRoomList;
    ArrayList<String> arrRoom;
    ArrayAdapter adapter;

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
                        JSONArray room = jsonObject.getJSONArray("room");
                        for (int i = 0; i < room.length(); i++){
                            JSONObject name = room.getJSONObject(i);
                            String roomName = name.getString("name");
                            arrRoom.add(roomName);
                        }
                        adapter.notifyDataSetChanged();
                        Toast.makeText(RoomListActivity.this, arrRoom.toString(), Toast.LENGTH_SHORT).show();
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
        adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, arrRoom);
        lvRoomList.setAdapter(adapter);
    }

//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//        finish();
//    }
}