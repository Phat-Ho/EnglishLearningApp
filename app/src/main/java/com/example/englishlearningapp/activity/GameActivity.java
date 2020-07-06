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

import com.example.englishlearningapp.R;
import com.example.englishlearningapp.utils.GlobalVariable;
import com.github.nkzawa.emitter.Emitter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class GameActivity extends AppCompatActivity {
    private static final String TAG = "GameActivity";
    TextView txtPlayerNumber, txtTimer, txtCurrentWord;
    EditText edtWord;
    ImageButton imgBtnSend;
    GlobalVariable globalVariable;
    ListView lvPlayerLeft;
    int roomId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        InitialView();
        GetIntentData();
        HandleSendWord();
        globalVariable = GlobalVariable.getInstance(this);
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
                        String currentWord = gameObj.getString("currentWord");
                        Log.d(TAG, "currentWord: " + currentWord);
                        txtCurrentWord.setText(currentWord);
                        Log.d(TAG, "text: " + txtCurrentWord.getText().toString());
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
        txtCurrentWord.setText(intent.getStringExtra("currentWord"));
    }

    private void HandleSendWord() {
        final String word = edtWord.getText().toString();
        imgBtnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                globalVariable.mSocket.emit("sendWord", word);
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