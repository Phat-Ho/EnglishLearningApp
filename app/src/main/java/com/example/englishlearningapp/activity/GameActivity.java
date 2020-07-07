package com.example.englishlearningapp.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.englishlearningapp.R;
import com.example.englishlearningapp.adapters.PlayerListGameAdapter;
import com.example.englishlearningapp.models.Player;
import com.example.englishlearningapp.utils.GlobalVariable;
import com.example.englishlearningapp.utils.LoginManager;
import com.github.nkzawa.emitter.Emitter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class GameActivity extends AppCompatActivity {
    private static final String TAG = "GameActivity";
    TextView txtPlayerNumber, txtTimer, txtCurrentWord;
    EditText edtWord;
    ImageButton imgBtnSend;
    GlobalVariable globalVariable;
    ListView lvPlayerLeft;
    ArrayList<Player> playerList = new ArrayList<>();
    PlayerListGameAdapter playerAdapter;
    int roomId;
    LoginManager loginManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        InitialView();
        GetIntentData();
        SetUpListView();
        HandleSendWord();
        globalVariable = GlobalVariable.getInstance(this);
        loginManager = new LoginManager(this);
    }

    private void SetUpListView() {
        playerAdapter = new PlayerListGameAdapter(this, playerList);
        lvPlayerLeft.setAdapter(playerAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: ");
        globalVariable.mSocket.on("sendGame", onSendGame);
        globalVariable.mSocket.on("sendTimer", onSendTimer);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        globalVariable.mSocket.off("sendGame", onSendGame);
        globalVariable.mSocket.off("sendTimer", onSendTimer);
    }

    private Emitter.Listener onSendGame = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, "send game: " + args[0].toString());
                    JSONObject gameObj = (JSONObject) args[0];
                    try {
                        JSONArray playerOrderArray = gameObj.getJSONArray("playerOrder");
                        ArrayList<Player> temp = new ArrayList<>();
                        int eliminatedId = gameObj.getInt("eliminatedPlayer");
                        if(eliminatedId == loginManager.getUserId()){
                            Toast.makeText(GameActivity.this, "You have been eliminated", Toast.LENGTH_SHORT).show();
                        }
                        int length = playerOrderArray.length();
                        if(length > 0) {
                            for (int i = 0; i < length; i++) {
                                int id = playerOrderArray.getJSONObject(i).getInt("playerId");
                                String name = playerOrderArray.getJSONObject(i).getString("playerName");
                                Player player = new Player(id, name);
                                temp.add(player);
                            }
                        }
                        playerList.clear();
                        playerList.addAll(temp);
                        playerAdapter.notifyDataSetChanged();

                        if(length == 1){
                            Toast.makeText(GameActivity.this, playerList.get(0).getName() + " is the winner", Toast.LENGTH_SHORT).show();
                        }

                        String currentWord = gameObj.getString("currentWord");
                        txtCurrentWord.setText(currentWord);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };

    private Emitter.Listener onSendTimer = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String timeLeft = args[0].toString();
                    Log.d(TAG, "send timer: " + timeLeft);
                    txtTimer.setText(timeLeft);
                }
            });
        }
    };

    private void GetIntentData() {
        Intent intent = getIntent();
        roomId = intent.getIntExtra("roomId", 0);
        playerList = (ArrayList<Player>) intent.getSerializableExtra("playerList");
        txtCurrentWord.setText(intent.getStringExtra("currentWord"));
    }

    private void HandleSendWord() {
        imgBtnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String word = edtWord.getText().toString();
                Log.d(TAG, "HandleSendWord: " + word);
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("word", word);
                    jsonObject.put("id", roomId);
                    jsonObject.put("playerId", loginManager.getUserId());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                globalVariable.mSocket.emit("sendWord", jsonObject);
                edtWord.setText(null);
            }
        });
    }

    private void InitialView() {
        txtPlayerNumber = findViewById(R.id.txt_game_player_number);
        txtTimer = findViewById(R.id.txt_game_timer);
        txtCurrentWord = findViewById(R.id.txt_game_previous_word);
        edtWord = findViewById(R.id.edt_game_word);
        imgBtnSend = findViewById(R.id.btn_game_send);
        lvPlayerLeft = findViewById(R.id.lv_game_player_left);
    }
}