package com.example.englishlearningapp.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.example.englishlearningapp.R;
import com.example.englishlearningapp.adapters.RoomAdapter;
import com.example.englishlearningapp.models.Player;
import com.example.englishlearningapp.models.Room;
import com.example.englishlearningapp.utils.DatabaseAccess;
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
    DatabaseAccess databaseAccess;
    boolean playerType;
    int playerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_list);
        globalVariable = GlobalVariable.getInstance(this);
        databaseAccess = DatabaseAccess.getInstance(this);
        initView();
        SetUpToolbar();
        loginManager = new LoginManager(this);
        playerId = loginManager.getUserId();
        globalVariable.mSocket.emit("getRoom");
        globalVariable.mSocket.on("roomList", onRetrieveRoomList);
        globalVariable.mSocket.once("sendRoomInfo", onSendRoomInfo);
        globalVariable.mSocket.once("sendViewGame", onSendViewGame);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        globalVariable.mSocket.off("roomList", onRetrieveRoomList);
        globalVariable.mSocket.off("sendRoomInfo", onSendRoomInfo);
        globalVariable.mSocket.off("sendViewGame", onSendViewGame);
    }

    private Emitter.Listener onSendViewGame = new Emitter.Listener() {
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
                                boolean isPlay = playersOrder.getJSONObject(i).getBoolean("isPlay");
                                if(isPlay){
                                    Player player = new Player(id, name, isPlay);
                                    temp.add(player);
                                }
                            }
                        }
                        long date = gameObj.getLong("date");
                        String roomName = gameObj.getString("roomName");
                        long gameDBId = databaseAccess.addGame(date, roomName);
                        int gameId = gameObj.getInt("id");
                        Intent gameIntent = new Intent(RoomListActivity.this, GameActivity.class);
                        gameIntent.putExtra("currentWord", currentWord);
                        gameIntent.putExtra("gameId", gameId);
                        gameIntent.putExtra("playerList", temp);
                        gameIntent.putExtra("gameDBId", gameDBId);
                        gameIntent.putExtra("isPlay", false);
                        globalVariable.mSocket.off("sendRoomInfo", onSendRoomInfo);
                        globalVariable.mSocket.off("sendViewGame", onSendViewGame);
                        startActivity(gameIntent);
                        finish();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };

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
                            String creator = object.getString("owner");
                            boolean isPlaying = object.getBoolean("isStart");
                            Room room = new Room(id, name, numOfPlayers, password, time, playerArr.length(), creator, isPlaying);
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
                        int time = roomObj.getInt("time");
                        int length = playerArray.length();
                        if(length > 0){
                            ArrayList<Player> temp = new ArrayList<>();
                            for (int i = 0; i < length; i++) {
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
                            Intent roomInfoIntent = new Intent(RoomListActivity.this, RoomInfoActivity.class);
                            roomInfoIntent.putExtra("roomId", roomId);
                            roomInfoIntent.putExtra("playerList", temp);
                            roomInfoIntent.putExtra("roomOwner", roomOwner);
                            roomInfoIntent.putExtra("roomName", roomName);
                            roomInfoIntent.putExtra("isPlay", playerType);
                            roomInfoIntent.putExtra("time", time);
                            globalVariable.mSocket.off("roomList", onRetrieveRoomList);
                            globalVariable.mSocket.off("sendRoomInfo", onSendRoomInfo);
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
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                final int roomId = arrRoom.get(position).getId();
                final String playerName = loginManager.getUserName();
                if(arrRoom.get(position).isPlaying()){
                    JSONObject jsonObject = new JSONObject();
                    JSONObject playerObj = new JSONObject();
                    JSONObject joinGameObj = new JSONObject();
                    try {
                        jsonObject.put("roomId", roomId);
                        playerObj.put("playerId", loginManager.getUserId());
                        playerObj.put("playerName", playerName);
                        playerObj.put("isPlay", false);
                        jsonObject.put("player", playerObj);
                        joinGameObj.put("gameId", roomId + 10000);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    globalVariable.mSocket.emit("getGameInfo", roomId);
                    globalVariable.mSocket.emit("joinGame", joinGameObj);
                }else{
                    final AlertDialog.Builder builder = new AlertDialog.Builder(RoomListActivity.this);
                    builder.setTitle("Chọn kiểu tham gia");
                    builder.setPositiveButton("Vào chơi", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if(arrRoom.get(position).getPlayerCount() >= arrRoom.get(position).getNumOfPlayers()){
                                showAlert("Phòng đã đầy", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                    }
                                });
                            }else{
                                JSONObject jsonObject = new JSONObject();
                                JSONObject playerObj = new JSONObject();
                                try {
                                    jsonObject.put("roomId", roomId);
                                    playerObj.put("playerId", loginManager.getUserId());
                                    playerObj.put("playerName", playerName);
                                    playerObj.put("isPlay", true);
                                    jsonObject.put("player", playerObj);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                globalVariable.mSocket.emit("joinRoom", jsonObject);
                            }
                        }
                    });
                    builder.setNegativeButton("Vào xem", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            JSONObject jsonObject = new JSONObject();
                            JSONObject playerObj = new JSONObject();
                            try {
                                jsonObject.put("roomId", roomId);
                                playerObj.put("playerId", loginManager.getUserId());
                                playerObj.put("playerName", playerName);
                                playerObj.put("isPlay", false);
                                jsonObject.put("player", playerObj);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            globalVariable.mSocket.emit("joinRoom", jsonObject);
                        }
                    });
                    final AlertDialog dialog = builder.create();
                    dialog.show();
                }
            }
        });
    }

    private void showAlert(String title, DialogInterface.OnClickListener listener){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setPositiveButton("OK", listener);
        final AlertDialog dialog = builder.create();
        dialog.show();
        final Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        LinearLayout.LayoutParams positiveButtonLL = (LinearLayout.LayoutParams) positiveButton.getLayoutParams();
        positiveButtonLL.width = ViewGroup.LayoutParams.MATCH_PARENT;
        positiveButton.setLayoutParams(positiveButtonLL);
    }

    private void SetUpToolbar() {
        toolbarRoomList.setTitle("");
        setSupportActionBar(toolbarRoomList);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            toolbarRoomList.getNavigationIcon().setColorFilter(getColor(android.R.color.white), PorterDuff.Mode.SRC_IN);
        }
        toolbarRoomList.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

}