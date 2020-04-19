package com.example.englishlearningapp.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.englishlearningapp.R;
import com.example.englishlearningapp.models.Word;
import com.example.englishlearningapp.utils.DatabaseAccess;
import com.example.englishlearningapp.utils.DatabaseContract;
import com.example.englishlearningapp.utils.LoginManager;
import com.example.englishlearningapp.utils.Server;
import com.like.LikeButton;
import com.like.OnLikeListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MeaningActivity extends AppCompatActivity {
    private static final String TAG = "MeaningActivity";
    private static boolean rememberChange = false;
    TextView txtWordHtml;
    ImageButton imgBtnPronounce;
    AutoCompleteTextView txtMeaningSearch;
    Toolbar meaningToolbar;
    TextToSpeech tts;
    LikeButton likeBtn;
    DatabaseAccess databaseAccess;
    CheckBox cbRemembered;
    LoginManager loginManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meaning);
        MappingView();
        SetUpToolbar();
        SetMeaningData();
        SetAutoCompleteSearchBox();
        loginManager = new LoginManager(this);
    }

    private void SetUpToolbar() {
        setSupportActionBar(meaningToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        meaningToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void SetAutoCompleteSearchBox() {
        final ArrayAdapter searchBoxAdapter = new ArrayAdapter(MeaningActivity.this, android.R.layout.simple_list_item_1);
        txtMeaningSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                ArrayList<Word> wordList = databaseAccess.getWords(s.toString());
                searchBoxAdapter.clear();
                searchBoxAdapter.addAll(wordList);
                txtMeaningSearch.setAdapter(searchBoxAdapter);
                txtMeaningSearch.setThreshold(1);
                searchBoxAdapter.notifyDataSetChanged();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        txtMeaningSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ArrayList<Word> word = databaseAccess.getWords(txtMeaningSearch.getText().toString());
                saveHistory(word.get(0).getId(), loginManager.getUserId());
                RefreshScreen(word.get(0).getWord(), word.get(0).getHtml());
            }
        });
    }

    private void RefreshScreen(String word, String html) {
        Intent intent = new Intent(this, MeaningActivity.class);
        intent.putExtra("word", word);
        intent.putExtra("html", html);
        startActivity(intent);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if(intent.hasExtra("word")){
            String html = intent.getStringExtra("wordHtml");
            final String word = intent.getStringExtra("word");
            addToFavorite(intent);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                txtWordHtml.setText(Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY));
            } else {
                txtWordHtml.setText(Html.fromHtml(html));
            }

            imgBtnPronounce.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    tts.speak(word, TextToSpeech.QUEUE_FLUSH, null, "");
                }
            });
        }
    }

    boolean isSaved;
    String globalWord = "";

    private void SetMeaningData() {
        Intent intent = getIntent();
        String wordHtml = intent.getStringExtra("wordHtml");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            txtWordHtml.setText(Html.fromHtml(wordHtml, Html.FROM_HTML_MODE_LEGACY));
        } else {
            txtWordHtml.setText(Html.fromHtml(wordHtml));
        }

        final String word = intent.getStringExtra("word");
        globalWord = word;
        databaseAccess = DatabaseAccess.getInstance(this);
        databaseAccess.open();
        imgBtnPronounce.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tts.speak(word, TextToSpeech.QUEUE_FLUSH, null, "");
            }
        });

        //Compare to set Favorite
        addToFavorite(intent);
        addToRemembered();
    }

    private void MappingView() {
        txtMeaningSearch = findViewById(R.id.meaning_auto_complete_search_box);
        txtWordHtml = findViewById(R.id.textWordHtml);
        imgBtnPronounce = findViewById(R.id.imageButtonPronounce);
        likeBtn = findViewById(R.id.LikeButtonHeart);
        cbRemembered = findViewById(R.id.checkBoxRemembered);
        meaningToolbar = findViewById(R.id.meaning_toolbar);
        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR){
                    tts.setLanguage(Locale.ENGLISH);
                }
            }
        });
    }

    int wordId = 0;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        tts.shutdown();
    }

    private void addToFavorite(Intent intent){
       wordId = intent.getIntExtra("id", 0);

        ArrayList<Word> favoriteWords = databaseAccess.getFavoriteWords();
        isSaved = false;
        for (int i = 0; i < favoriteWords.size(); i++){
            if (wordId == favoriteWords.get(i).getId()){
                isSaved = true;
                Log.d("AAA", "Word is saved");
                break;
            }
        }
        if (isSaved == false){
            likeBtn.setLiked(false);
        } else {
            likeBtn.setLiked(true);
        }
        likeBtn.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {
                if (isSaved == false) {
                    databaseAccess.addFavorite(wordId, DatabaseContract.NOT_SYNC);
                }

            }

            @Override
            public void unLiked(LikeButton likeButton) {
                Toast.makeText(MeaningActivity.this, "Removed \"" + globalWord + "\" from you Favorite", Toast.LENGTH_SHORT).show();
                databaseAccess.removeFavorite(wordId);
            }
        });
    }
    private void addToRemembered(){
        Intent intent = getIntent();
        int isRemembered = intent.getIntExtra("remembered", 0);
        if (isRemembered == 1){
            cbRemembered.setChecked(true);
        } else {
            cbRemembered.setChecked(false);
        }

        cbRemembered.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                MeaningActivity.rememberChange = true;
                if (isChecked){
                    databaseAccess.updateHistoryRemembered(wordId, 1);
                } else {
                    databaseAccess.updateHistoryRemembered(wordId, 0);
                }
            }
        });
    }

    public void saveHistory(final int wordID, final int pUserID){
        //Nếu có internet và đã login thì add vô server vào local với sync status = success
        if(Server.haveNetworkConnection(this) && pUserID > 0){
            final String currentDateTime = getDatetime();
            String url = Server.ADD_HISTORY_URL;
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        String message = jsonObject.getString("message");
                        if(message.equals("success")){
                            databaseAccess.addHistory(wordID, DatabaseContract.SYNC, currentDateTime);
                            Log.d(TAG, "onResponse: added to server");
                        }else{
                            databaseAccess.addHistory(wordID, DatabaseContract.NOT_SYNC, currentDateTime);
                            Log.d(TAG, "onResponse: " + message);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d(TAG, "onErrorResponse: " + error.getMessage());
                    databaseAccess.addHistory(wordID, DatabaseContract.NOT_SYNC, currentDateTime);
                }
            }){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    HashMap<String, String> params = new HashMap<>();
                    params.put("userid", String.valueOf(pUserID));
                    params.put("wordid", String.valueOf(wordID));
                    params.put("datetime", currentDateTime);
                    params.put("sync", String.valueOf(DatabaseContract.SYNC));

                    return params;
                }
            };
            requestQueue.add(stringRequest);
        }else{ //Nếu không có internet hoặc chưa login thì add vô local với sync status = fail
            databaseAccess.addHistory(wordID, DatabaseContract.NOT_SYNC, getDatetime());
            Log.d(TAG, "saveHistory: no internet or no login, add to local");
        }
    }

    public String getDatetime(){
        java.text.SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        if(rememberChange){
            setResult(Activity.RESULT_OK, intent);
        }else{
            setResult(Activity.RESULT_CANCELED, intent);
        }
        finish();
    }
}
