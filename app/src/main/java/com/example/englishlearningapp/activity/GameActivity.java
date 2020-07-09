package com.example.englishlearningapp.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.englishlearningapp.R;
import com.example.englishlearningapp.adapters.PlayerListGameAdapter;
import com.example.englishlearningapp.models.Player;
import com.example.englishlearningapp.models.Word;
import com.example.englishlearningapp.utils.DatabaseAccess;
import com.example.englishlearningapp.utils.GlobalVariable;
import com.example.englishlearningapp.utils.LoginManager;
import com.github.nkzawa.emitter.Emitter;
import com.google.android.material.button.MaterialButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class GameActivity extends AppCompatActivity {
    private static final String TAG = "GameActivity";
    TextView txtPlayerNumber, txtTimer, txtCurrentWord, txtWordDetail, txtPlayersOrder;
    EditText edtWord;
    ImageButton imgBtnSend;
    TextView txtResult, txtNextPlayer;
    MaterialButton btnExit, btnContinue, btnGameExit, btnGameContinue;
    GlobalVariable globalVariable;
    ListView lvPlayerLeft;
    FrameLayout gameFrameLayout;
    LinearLayout gameBtnWrapper;
    ArrayList<Player> playerList = new ArrayList<>();
    PlayerListGameAdapter playerAdapter;
    int gameId;
    LoginManager loginManager;
    DatabaseAccess databaseAccess;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        globalVariable = GlobalVariable.getInstance(this);
        loginManager = new LoginManager(this);
        databaseAccess = DatabaseAccess.getInstance(this);
        InitialView();
        GetIntentData();
        SetUpListView();
        HandleButtonEvent();
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
        globalVariable.mSocket.on("sendResult", onSendResult);
        globalVariable.mSocket.once("sendGameEnd", onSendGameEnd);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: game activity");
        globalVariable.mSocket.off("sendGame", onSendGame);
        globalVariable.mSocket.off("sendTimer", onSendTimer);
        globalVariable.mSocket.off("sendGameEnd");
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("gameId", gameId);
            jsonObject.put("playerId", loginManager.getUserId());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        globalVariable.mSocket.emit("leaveGame", jsonObject);
    }

    private Emitter.Listener onSendResult = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, "send result: " + args[0].toString());
                    JSONObject resultObj = (JSONObject) args[0];
                    try {
                        String isCorrect = resultObj.getString("isCorrect");
                        String nextPlayer = resultObj.getString("nextPlayer");
                        int eliminatedPlayerId = resultObj.getInt("eliminatedPlayerId");
                        showResult(isCorrect, nextPlayer, eliminatedPlayerId);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };

    private Emitter.Listener onSendGameEnd = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, "send game end: " + args[0].toString());
                    globalVariable.mSocket.off("sendGame", onSendGame);
                    globalVariable.mSocket.off("sendTimer", onSendTimer);
                    String winner = args[0].toString();
                    gameFrameLayout.setVisibility(View.VISIBLE);
                    txtResult.setVisibility(View.INVISIBLE);
                    txtNextPlayer.setText(winner + " là người chiến thắng");
                    txtNextPlayer.setTextColor(getResources().getColor(R.color.colorRed));
                    gameBtnWrapper.setVisibility(View.VISIBLE);
                    lvPlayerLeft.setVisibility(View.GONE);
                    txtPlayersOrder.setVisibility(View.GONE);
                    btnExit.setVisibility(View.GONE);
                    btnContinue.setVisibility(View.GONE);
                    txtTimer.setVisibility(View.INVISIBLE);
                }
            });
        }
    };

    private void showResult(String pIsCorrect, String pNextPlayer, int playerId){
        gameFrameLayout.setVisibility(View.VISIBLE);
        if(pIsCorrect.equals("true")){
            txtResult.setTextColor(getResources().getColor(R.color.colorGreen));
            txtResult.setText("Đúng");
            txtNextPlayer.setText("Tiếp theo: " + pNextPlayer);
        }
        if(pIsCorrect.equals("false")){
            txtResult.setTextColor(getResources().getColor(R.color.colorRed));
            txtResult.setText("Sai");
            if(playerId == loginManager.getUserId()){
                globalVariable.mSocket.off("sendGame");
                globalVariable.mSocket.off("sendResult");
                globalVariable.mSocket.off("sendTimer");
                txtNextPlayer.setText("Bạn đã bị loại");
                btnExit.setVisibility(View.VISIBLE);
                btnContinue.setVisibility(View.VISIBLE);
            }else{
                txtNextPlayer.setText("Tiếp theo: " + pNextPlayer);
                btnExit.setVisibility(View.GONE);
                btnContinue.setVisibility(View.GONE);
            }
        }
        if(pIsCorrect.equals("timeOut")){
            txtResult.setText("Hết giờ");
            if(playerId == loginManager.getUserId()){
                globalVariable.mSocket.off("sendGame");
                globalVariable.mSocket.off("sendResult");
                globalVariable.mSocket.off("sendTimer");
                txtNextPlayer.setText("Bạn đã bị loại");
                btnExit.setVisibility(View.VISIBLE);
                btnContinue.setVisibility(View.VISIBLE);
            }else{
                txtNextPlayer.setText("Tiếp theo: " + pNextPlayer);
                btnExit.setVisibility(View.GONE);
                btnContinue.setVisibility(View.GONE);
            }
        }
    }

    private Emitter.Listener onSendGame = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, "send game: " + args[0].toString());
                    gameFrameLayout.setVisibility(View.GONE);
                    JSONObject gameObj = (JSONObject) args[0];
                    try {
                        JSONArray playerOrderArray = gameObj.getJSONArray("playerOrder");

                        if(gameObj.has("leavePlayer")){
                            String leavePlayer = gameObj.getString("leavePlayer");
                            Toast.makeText(GameActivity.this, leavePlayer + " had left", Toast.LENGTH_SHORT).show();
                        }

                        if(playerOrderArray.getJSONObject(0).getInt("playerId") != loginManager.getUserId() || playerOrderArray.length() == 1){
                            edtWord.setEnabled(false);
                            imgBtnSend.setEnabled(false);
                        }else {
                            edtWord.setEnabled(true);
                            imgBtnSend.setEnabled(true);
                        }

                        int length = playerOrderArray.length();
                        if(length > 0) {
                            ArrayList<Player> temp = new ArrayList<>();
                            for (int i = 0; i < length; i++) {
                                int id = playerOrderArray.getJSONObject(i).getInt("playerId");
                                String name = playerOrderArray.getJSONObject(i).getString("playerName");
                                Player player = new Player(id, name);
                                temp.add(player);
                            }
                            playerList.clear();
                            playerList.addAll(temp);
                            playerAdapter.notifyDataSetChanged();
                        }

                        String currentWord = gameObj.getString("currentWord");
                        ArrayList<Word> words = databaseAccess.getWordExactly(currentWord);
                        String description = words.get(0).getDescription();
                        txtWordDetail.setText(description);
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
                    txtTimer.setText(timeLeft + "s");
                }
            });
        }
    };

    private void GetIntentData() {
        Intent intent = getIntent();
        gameId = intent.getIntExtra("gameId", 0);
        playerList = (ArrayList<Player>) intent.getSerializableExtra("playerList");

        if(playerList.get(0).getId() != loginManager.getUserId()){
            edtWord.setEnabled(false);
            imgBtnSend.setEnabled(false);
        }else {
            edtWord.setEnabled(true);
            imgBtnSend.setEnabled(true);
        }
        String currentWord = intent.getStringExtra("currentWord");
        txtCurrentWord.setText(currentWord);
        ArrayList<Word> words = databaseAccess.getWordExactly(currentWord);
        String description = words.get(0).getDescription();
        txtWordDetail.setText(description);
    }

    private void HandleButtonEvent() {
        imgBtnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String word = edtWord.getText().toString();
                Log.d(TAG, "HandleSendWord: " + word);
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("word", word);
                    jsonObject.put("id", gameId);
                    jsonObject.put("playerId", loginManager.getUserId());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                globalVariable.mSocket.emit("sendWord", jsonObject);
                edtWord.setText(null);
            }
        });

        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                globalVariable.mSocket.on("sendGame", onSendGame);
                globalVariable.mSocket.on("sendTimer", onSendTimer);
                globalVariable.mSocket.on("sendResult", onSendResult);
                gameFrameLayout.setVisibility(View.GONE);
            }
        });

        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnGameExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnGameContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject obj = new JSONObject();
                try {
                    obj.put("gameId", gameId);
                    obj.put("playerId", loginManager.getUserId());
                    obj.put("playerName", loginManager.getUserName());
                    globalVariable.mSocket.emit("continueGame", obj);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void InitialView() {
        gameBtnWrapper = findViewById(R.id.game_button_wrapper);
        txtPlayersOrder = findViewById(R.id.txtPlayersOrder);
        txtWordDetail = findViewById(R.id.txt_game_word_detail);
        txtPlayerNumber = findViewById(R.id.txt_game_player_number);
        txtTimer = findViewById(R.id.txt_game_timer);
        txtCurrentWord = findViewById(R.id.txt_game_previous_word);
        edtWord = findViewById(R.id.edt_game_word);
        imgBtnSend = findViewById(R.id.btn_game_send);
        lvPlayerLeft = findViewById(R.id.lv_game_player_left);
        txtResult = findViewById(R.id.txt_result);
        txtNextPlayer = findViewById(R.id.txt_next_player);
        btnExit = findViewById(R.id.btn_result_exit);
        btnContinue = findViewById(R.id.btn_result_continue);
        gameFrameLayout = findViewById(R.id.game_frame_layout);
        btnGameContinue = findViewById(R.id.btn_game_continue);
        btnGameExit = findViewById(R.id.btn_game_exit);
    }
}