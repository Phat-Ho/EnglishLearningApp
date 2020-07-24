package com.example.englishlearningapp.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.englishlearningapp.R;
import com.example.englishlearningapp.adapters.PlayerListGameAdapter;
import com.example.englishlearningapp.models.HistoryGameWord;
import com.example.englishlearningapp.models.Player;
import com.example.englishlearningapp.models.Word;
import com.example.englishlearningapp.utils.DatabaseAccess;
import com.example.englishlearningapp.utils.GlobalVariable;
import com.example.englishlearningapp.utils.LoginManager;
import com.example.englishlearningapp.utils.Server;
import com.github.nkzawa.emitter.Emitter;
import com.google.android.material.button.MaterialButton;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;

public class GameActivity extends AppCompatActivity {
    private static final String TAG = "GameActivity";
    TextView txtTimer, txtCurrentWord, txtWordDetail, txtPlayersOrder;
    EditText edtWord;
    ImageButton imgBtnSend;
    TextView txtResult, txtNextPlayer;
    MaterialButton btnExit, btnContinue, btnGameExit, btnGameContinue, btnGameHistory, btnShowMore;
    GlobalVariable globalVariable;
    ListView lvPlayerLeft;
    FrameLayout gameFrameLayout;
    CardView cardPlayerOrder;
    Dialog gamePopup;
    LinearLayout gameBtnWrapper, gameLinearLayout;
    ArrayList<Player> playerList = new ArrayList<>();
    PlayerListGameAdapter playerAdapter;
    int gameId;
    long gameDBId;
    boolean isPlay;
    LoginManager loginManager;
    DatabaseAccess databaseAccess;
    TextView txtPopupWord, txtPopupWordMeaning;
    ImageButton imgBtnPopUpPronoun;
    ImageView imgPopupWord;
    TextToSpeech textToSpeech;
    YouTubePlayerView youTubePlayerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        globalVariable = GlobalVariable.getInstance(this);
        loginManager = new LoginManager(this);
        databaseAccess = DatabaseAccess.getInstance(this);
        gamePopup = new Dialog(this);
        InitialView();
        GetIntentData();
        SetUpListView();
        HandleButtonEvent();
        globalVariable.mSocket.on("sendGame", onSendGame);
        globalVariable.mSocket.on("sendTimer", onSendTimer);
        globalVariable.mSocket.on("sendResult", onSendResult);
        globalVariable.mSocket.once("sendNewRoomInfo", onSendRoomInfo);
        globalVariable.mSocket.once("sendGameEnd", onSendGameEnd);
        globalVariable.mSocket.on("sendHistoryWord", onSendHistoryWord);
    }

    private void SetUpListView() {
        playerAdapter = new PlayerListGameAdapter(this, playerList);
        lvPlayerLeft.setAdapter(playerAdapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: game activity");
        globalVariable.mSocket.off("sendGame", onSendGame);
        globalVariable.mSocket.off("sendTimer", onSendTimer);
        globalVariable.mSocket.off("sendResult", onSendResult);
        globalVariable.mSocket.off("sendHistoryWord", onSendHistoryWord);
        globalVariable.mSocket.off("sendNewRoomInfo", onSendRoomInfo);
        if(isPlay){
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("gameId", gameId);
                jsonObject.put("playerId", loginManager.getUserId());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.d(TAG, "emit leave game");
            globalVariable.mSocket.emit("leaveGame", jsonObject);
        }
        textToSpeech.shutdown();
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

    private Emitter.Listener onSendHistoryWord = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, "send history word: " + args[0].toString());
                    JSONArray historyArr = (JSONArray) args[0];
                    try {
                        int len = historyArr.length();
                        if(len > 0){
                            ArrayList<HistoryGameWord> temp = new ArrayList<>();
                            for (int i = 0; i < len; i++) {
                                String word = historyArr.getJSONObject(i).getString("word");
                                String playerName = historyArr.getJSONObject(i).getString("playerName");
                                HistoryGameWord historyWord = new HistoryGameWord(word, playerName);
                                temp.add(historyWord);
                            }

                            Intent historyIntent = new Intent(GameActivity.this, InGameHistoryActivity.class);
                            historyIntent.putExtra("gameHistoryWord", temp);
                            globalVariable.mSocket.off("sendGame", onSendGame);
                            globalVariable.mSocket.off("sendTimer", onSendTimer);
                            globalVariable.mSocket.off("sendResult", onSendResult);
                            startActivity(historyIntent);
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
                    Log.d(TAG, "send new room info: " + args[0].toString());
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
                                if(isPlay){
                                    Player player = new Player(id, name, isPlay);
                                    temp.add(player);
                                }
                            }
                            Intent roomInfoIntent = new Intent(GameActivity.this, RoomInfoActivity.class);
                            roomInfoIntent.putExtra("roomId", roomId);
                            roomInfoIntent.putExtra("playerList", temp);
                            roomInfoIntent.putExtra("roomOwner", roomOwner);
                            roomInfoIntent.putExtra("roomName", roomName);
                            roomInfoIntent.putExtra("time", time);
                            globalVariable.mSocket.off("sendGame", onSendGame);
                            globalVariable.mSocket.off("sendTimer", onSendTimer);
                            globalVariable.mSocket.off("sendResult", onSendResult);
                            globalVariable.mSocket.off("sendHistoryWord", onSendHistoryWord);
                            globalVariable.mSocket.off("sendNewRoomInfo", onSendRoomInfo);
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

    private Emitter.Listener onSendGameEnd = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @SuppressLint("ResourceType")
                @Override
                public void run() {
                    Log.d(TAG, "send game end: " + args[0].toString());
                    globalVariable.mSocket.off("sendGame", onSendGame);
                    globalVariable.mSocket.off("sendTimer", onSendTimer);
                    hideSoftKeyBoard();
                    JSONObject obj = (JSONObject) args[0];

                    String winner;
                    JSONArray array;
                    try {
                        winner = obj.getString("winner");
                        array = obj.getJSONArray("historyWord");
                        gameLinearLayout.setBackground(getDrawable(R.drawable.winner));
                        gameFrameLayout.setVisibility(View.VISIBLE);
                        btnGameContinue.setVisibility(View.VISIBLE);
                        txtResult.setVisibility(View.INVISIBLE);
                        txtNextPlayer.setText(winner + " chiến thắng");
                        txtNextPlayer.setTextColor(getResources().getColor(R.color.colorRed));
                        gameBtnWrapper.setVisibility(View.VISIBLE);
                        cardPlayerOrder.setVisibility(View.GONE);
                        btnExit.setVisibility(View.GONE);
                        btnContinue.setVisibility(View.GONE);
                        txtTimer.setVisibility(View.INVISIBLE);
                        edtWord.setEnabled(false);
                        int len = array.length();
                        for (int i = 0; i < len; i++) {
                            String word = array.getJSONObject(i).getString("word");
                            String playerName = array.getJSONObject(i).getString("playerName");
                            databaseAccess.addGameWord(gameDBId, word, playerName);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
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
            btnExit.setVisibility(View.GONE);
            btnContinue.setVisibility(View.GONE);
        }
        if(pIsCorrect.equals("false") || pIsCorrect.equals("timeOut") || pIsCorrect.equals("wordExisted")){
            txtResult.setTextColor(getResources().getColor(R.color.colorRed));
            if(pIsCorrect.equals("false")){
                txtResult.setText("Sai");
            }
            if(pIsCorrect.equals("timeOut")){
                txtResult.setText("Hết giờ");
            }
            if(pIsCorrect.equals("wordExisted")){
                txtResult.setText("Đã tồn tại");
            }

            if(playerId == loginManager.getUserId()){
                globalVariable.mSocket.off("sendGame");
                globalVariable.mSocket.off("sendResult");
                globalVariable.mSocket.off("sendTimer");
                txtNextPlayer.setText("Bạn đã bị loại");
                edtWord.setVisibility(View.GONE);
                imgBtnSend.setVisibility(View.GONE);
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
                            edtWord.setVisibility(View.GONE);
                            imgBtnSend.setVisibility(View.GONE);
                            hideSoftKeyBoard();
                        }else {
                            edtWord.setVisibility(View.VISIBLE);
                            imgBtnSend.setVisibility(View.VISIBLE);
                        }

                        int length = playerOrderArray.length();
                        if(length > 0) {
                            ArrayList<Player> temp = new ArrayList<>();
                            for (int i = 0; i < length; i++) {
                                int id = playerOrderArray.getJSONObject(i).getInt("playerId");
                                String name = playerOrderArray.getJSONObject(i).getString("playerName");
                                boolean isPlay = playerOrderArray.getJSONObject(i).getBoolean("isPlay");
                                Player player = new Player(id, name, isPlay);
                                temp.add(player);
                            }
                            playerList.clear();
                            playerList.addAll(temp);
                            playerAdapter.notifyDataSetChanged();
                        }

                        String currentWord = gameObj.getString("currentWord");
                        ArrayList<Word> words = databaseAccess.getWordExactly(currentWord);
                        String description = words.get(0).getDescription();
                        int len = description.length();
                        final String substring = description.substring(0, Math.min(len, 60));
                        if(len > 60){
                            txtWordDetail.setText(substring + "...");
                        }else{
                            txtWordDetail.setText(substring);
                        }
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
        gameDBId = intent.getLongExtra("gameDBId", 0);
        isPlay = intent.getBooleanExtra("isPlay", true);
        playerList = (ArrayList<Player>) intent.getSerializableExtra("playerList");

        if(playerList.get(0).getId() != loginManager.getUserId()){
            edtWord.setVisibility(View.GONE);
            imgBtnSend.setVisibility(View.GONE);
        }else {
            edtWord.setVisibility(View.VISIBLE);
            imgBtnSend.setVisibility(View.VISIBLE);
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
                    obj.put("isPlay", isPlay);
                    globalVariable.mSocket.emit("continueGame", obj);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        btnGameHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                globalVariable.mSocket.emit("getHistoryWord", gameId);
            }
        });

        btnShowMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopUp();
            }
        });
        gamePopup.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                Log.d(TAG, "onCancel popup");
                youTubePlayerView.release();
            }
        });
    }

    private void showPopUp(){
        gamePopup.setContentView(R.layout.popup_detail_game);
        txtPopupWord = gamePopup.findViewById(R.id.txt_game_word);
        txtPopupWordMeaning = gamePopup.findViewById(R.id.txt_game_word_meaning);
        imgBtnPopUpPronoun = gamePopup.findViewById(R.id.img_btn_game_pronounce);
        imgPopupWord = gamePopup.findViewById(R.id.img_game_word);
        youTubePlayerView = gamePopup.findViewById(R.id.yt_player_game);

        final String currentWord = txtCurrentWord.getText().toString();
        ArrayList<Word> words = databaseAccess.getWordExactly(currentWord);
        if(words.size() > 0){
            ProcessingHTML(words.get(0).getHtml());
            loadingImage(currentWord);
            imgBtnPopUpPronoun.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    textToSpeech.speak(currentWord, TextToSpeech.QUEUE_FLUSH, null, "");
                }
            });

            final String youtubeLink = words.get(0).getYoutubeLink();
            if(youtubeLink != null){
                if (youTubePlayerView != null){
                    youTubePlayerView.setVisibility(View.VISIBLE);
                    youTubePlayerView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
                        @Override
                        public void onReady(YouTubePlayer youTubePlayer) {
                            super.onReady(youTubePlayer);
                            youTubePlayer.loadVideo(youtubeLink, 0);
                        }
                    });
                }
            }else {
                youTubePlayerView.setVisibility(View.GONE);
            }
        }
        gamePopup.show();
    }

    private void ProcessingHTML(String contentHtml){
        int start = contentHtml.indexOf("<h1>");
        int end = contentHtml.indexOf("<h3>");
        String replacement = "";
        String toBeReplaced = contentHtml.substring(start, end);
        String wordHtml = toBeReplaced;
        String meaningHtml = contentHtml.replace(toBeReplaced, replacement);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            txtPopupWord.setText(Html.fromHtml(wordHtml, Html.FROM_HTML_MODE_LEGACY));
            txtPopupWordMeaning.setText(Html.fromHtml(meaningHtml, Html.FROM_HTML_MODE_LEGACY));
        } else {
            txtPopupWord.setText(Html.fromHtml(wordHtml));
            txtPopupWordMeaning.setText(Html.fromHtml(meaningHtml));
        }
    }

    private void loadingImage(String query){
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, Server.UNSPLASH_SEARCH + query, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "onResponse: " + response.toString());

                        try {
                            JSONArray resultArray = response.getJSONArray("results");
                            if(resultArray.length() > 0){
                                JSONObject urls = resultArray.getJSONObject(0).getJSONObject("urls");
                                String regular_url = urls.getString("regular");
                                Picasso.get().load(regular_url).into(imgPopupWord);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("AAA", error.toString());
                    }
                });
        requestQueue.add(jsonObjectRequest);
    }

    private void InitialView() {
        cardPlayerOrder = findViewById(R.id.card_player_order);
        gameLinearLayout = findViewById(R.id.game_linear_layout);
        gameBtnWrapper = findViewById(R.id.game_button_wrapper);
        txtPlayersOrder = findViewById(R.id.txtPlayersOrder);
        txtWordDetail = findViewById(R.id.txt_game_word_detail);
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
        btnGameHistory = findViewById(R.id.btn_game_history);
        btnShowMore = findViewById(R.id.btn_game_show_more);
        textToSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR){
                    textToSpeech.setLanguage(Locale.ENGLISH);
                }else{
                    Log.d(TAG, "onInit: Error");
                }
            }
        });
    }

    private void hideSoftKeyBoard() {
        View v = getWindow().getCurrentFocus();
        if (v != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    }
}