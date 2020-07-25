package com.example.englishlearningapp.activity;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.speech.tts.TextToSpeech;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.englishlearningapp.R;
import com.example.englishlearningapp.adapters.PopupHistoryAdapter;
import com.example.englishlearningapp.adapters.PopupRemindedAdapter;
import com.example.englishlearningapp.models.HistoryWord;
import com.example.englishlearningapp.models.MyDate;
import com.example.englishlearningapp.models.TopicWord;
import com.example.englishlearningapp.models.Word;
import com.example.englishlearningapp.utils.AlarmPropsManager;
import com.example.englishlearningapp.utils.DatabaseAccess;
import com.example.englishlearningapp.utils.DatabaseContract;
import com.example.englishlearningapp.utils.GlobalVariable;
import com.example.englishlearningapp.utils.LoginManager;
import com.example.englishlearningapp.utils.Server;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.button.MaterialButton;
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class MeaningActivity extends AppCompatActivity {
    private static final String TAG = "MeaningActivity";
    private static final int REQUEST_CODE_LOCATION = 1;
    private static boolean rememberChange = false;
    TextView txtWordHtml, txtContentHtml;
    ImageButton imgBtnPronounce;
    AutoCompleteTextView txtMeaningSearch;
    Toolbar meaningToolbar;
    TextToSpeech tts;
    LikeButton likeBtn;
    ImageView imgMeaning;
    DatabaseAccess databaseAccess = null;
    Dialog meaningPopup;
    LoginManager loginManager;
    AlarmPropsManager alarmPropsManager;
    PopupHistoryAdapter popupHistoryAdapter;
    PopupRemindedAdapter popupRemindedAdapter;
    ArrayList<MyDate> historyDateList, remindedDateList;
    private YouTubePlayerView ytPlayerView;
    YouTubePlayer ytPlayer = null;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GlobalVariable.changeStatusBarColor(MeaningActivity.this);
        setContentView(R.layout.activity_meaning);
        databaseAccess = DatabaseAccess.getInstance(this);
        MappingView();
        SetUpToolbar();
        meaningPopup = new Dialog(this);
        loginManager = new LoginManager(this);
        SetMeaningData();
        SetAutoCompleteSearchBox();
        alarmPropsManager = new AlarmPropsManager(this);

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void SetUpToolbar() {
        setSupportActionBar(meaningToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        meaningToolbar.getNavigationIcon().setColorFilter(getColor(android.R.color.white), PorterDuff.Mode.SRC_IN);
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
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ArrayList<Word> word = databaseAccess.getWords(txtMeaningSearch.getText().toString());
                String wordHeader = word.get(0).getWord();
                String wordHtml = word.get(0).getHtml();
                int wordId = word.get(0).getId();
                String ytLink = databaseAccess.getWordsById(wordId).getYoutubeLink();
                link = ytLink;
                if(ytPlayer != null){
                    ytPlayer.pause();

                }
                saveHistory(word.get(0).getId(), loginManager.getUserId());
                RefreshScreen(wordHeader, wordHtml, wordId, ytLink);
                hideSoftKeyBoard();
            }
        });
    }

    private void RefreshScreen(String word, String html, int wordId, String youtubeLink) {
        Intent intent = new Intent(this, MeaningActivity.class);
        intent.putExtra("word", word);
        intent.putExtra("html", html);
        intent.putExtra("id", wordId);
        intent.putExtra("youtubeLink", youtubeLink);
        startActivity(intent);
    }

    PlayVideo playVideo = new PlayVideo();

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.d("beanbean", "onNewIntent: ");

        if(intent.hasExtra("show_popup")){
            int wordId = intent.getIntExtra("id", 0);
            String word = intent.getStringExtra("word");
            String description = intent.getStringExtra("description");
            showPopup(wordId, word, description);
        }
        if(intent.hasExtra("word")){
            String contentHtml = intent.getStringExtra("html");
            final String word = intent.getStringExtra("word");
            final String ytLink = intent.getStringExtra("youtubeLink");
            if (ytPlayerView != null){
//                ytPlayerView.release();
                if (ytLink != null){
                    ytPlayerView.setVisibility(View.VISIBLE);
                    ytPlayerView.removeYouTubePlayerListener(playVideo);
                    ytPlayerView.addYouTubePlayerListener(playVideo);
                } else {
                    ytPlayerView.setVisibility(View.GONE);
                }
            }

            int start = contentHtml.indexOf("<h1>");
            int end = contentHtml.indexOf("<h2>");
            String replacement = "";
            String toBeReplaced = contentHtml.substring(start, end);
            String wordHtml = toBeReplaced;
            String meaningHtml = contentHtml.replace(toBeReplaced, replacement);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                txtWordHtml.setText(Html.fromHtml(wordHtml, Html.FROM_HTML_MODE_LEGACY));
                txtContentHtml.setText(Html.fromHtml(meaningHtml, Html.FROM_HTML_MODE_LEGACY));
            } else {
                txtWordHtml.setText(Html.fromHtml(wordHtml));
                txtContentHtml.setText(Html.fromHtml(meaningHtml));
            }

            loadingImage(word);
            imgBtnPronounce.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    tts.speak(word, TextToSpeech.QUEUE_FLUSH, null, "");
                }
            });
            addToFavorite(intent);
        }
    }

    boolean isSaved;
    String link = "";

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void SetMeaningData() {
        Intent intent = getIntent();
        databaseAccess = DatabaseAccess.getInstance(this);
        databaseAccess.open();
        if(intent.hasExtra("show_popup")){
            int wordId = intent.getIntExtra("id", 0);
            String word = intent.getStringExtra("word");
            String description = intent.getStringExtra("description");
            showPopup(wordId, word, description);
        }

        String contentHtml = "";

        CharSequence text = getIntent().getCharSequenceExtra(Intent.EXTRA_PROCESS_TEXT);
        if (text != null) {
            String translatedWord = text.toString();
            ArrayList<Word> dbWord = databaseAccess.getWords(translatedWord.trim().toLowerCase());
            if (dbWord.isEmpty()){
                txtWordHtml.setText("Không tìm thấy kết quả. Vui lòng bỏ /s/ và /ed/ khi tra từ");
                imgBtnPronounce.setVisibility(View.INVISIBLE);
            } else {
                int wordId = dbWord.get(0).getId();
                final String youtubeLink = databaseAccess.getWordsById(wordId).getYoutubeLink();

                link = youtubeLink;
                if (youtubeLink != null){
//                ytPlayerView.removeYouTubePlayerListener(playVideo);
                    ytPlayerView.addYouTubePlayerListener(playVideo);
                } else {
                    ytPlayerView.setVisibility(View.GONE);
                }
                contentHtml = dbWord.get(0).getHtml();
                ProcessingHTML(contentHtml);
                loadingImage(dbWord.get(0).getWord());
                saveHistory(dbWord.get(0).getId(), loginManager.getUserId());
            }
        } else {
            final String youtubeLink = intent.getStringExtra("youtubeLink");

            link = youtubeLink;
            if (youtubeLink != null){
//                ytPlayerView.removeYouTubePlayerListener(playVideo);
                ytPlayerView.addYouTubePlayerListener(playVideo);
            } else {
                ytPlayerView.setVisibility(View.GONE);
            }

            contentHtml = intent.getStringExtra("html");
            if(contentHtml != null){
                ProcessingHTML(contentHtml);
            }
            final String word = intent.getStringExtra("word");
            loadingImage(word);
        }

        final String word = intent.getStringExtra("word");
        imgBtnPronounce.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tts.speak(word, TextToSpeech.QUEUE_FLUSH, null, "");
            }
        });

        //Compare to set Favorite
        addToFavorite(intent);
    }

    private class PlayVideo extends AbstractYouTubePlayerListener {

        @Override
        public void onReady(YouTubePlayer youTubePlayer) {
            Log.d("beanbean", "onReady: ");
            ytPlayer = youTubePlayer;
            youTubePlayer.loadVideo(link, 0);
        }

        @Override
        public void onStateChange(final YouTubePlayer youTubePlayer, PlayerConstants.PlayerState state) {
            super.onStateChange(youTubePlayer, state);
            ytPlayer = youTubePlayer;
            Log.d("beanbean", "onStateChange: " + state.toString());
            if(state.toString().equals("PAUSED")){
                Log.d(TAG, "onLink: " + link);
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        youTubePlayer.loadVideo(link, 0);

                    }
                };
                Handler handler = new Handler();
                handler.postDelayed(runnable, 1000);
            }
        }
    }

    private void ProcessingHTML(String contentHtml){
        int start = contentHtml.indexOf("<h1>");
        int end = contentHtml.indexOf("<h3>");
        String replacement = "";
        String toBeReplaced = contentHtml.substring(start, end);
        String wordHtml = toBeReplaced;
        String meaningHtml = contentHtml.replace(toBeReplaced, replacement);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            txtWordHtml.setText(Html.fromHtml(wordHtml, Html.FROM_HTML_MODE_LEGACY));
            txtContentHtml.setText(Html.fromHtml(meaningHtml, Html.FROM_HTML_MODE_LEGACY));
        } else {
            txtWordHtml.setText(Html.fromHtml(wordHtml));
            txtContentHtml.setText(Html.fromHtml(meaningHtml));
        }
    }

    private void MappingView() {
        txtMeaningSearch = findViewById(R.id.meaning_auto_complete_search_box);
        txtWordHtml = findViewById(R.id.textWordHtml);
        txtContentHtml = findViewById(R.id.textViewContentHtml);
        imgBtnPronounce = findViewById(R.id.imageButtonPronounce);
        likeBtn = findViewById(R.id.LikeButtonHeart);
        imgMeaning = findViewById(R.id.imageViewMeaning);
        meaningToolbar = findViewById(R.id.meaning_toolbar);
        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR){
                    tts.setLanguage(Locale.ENGLISH);
                }
            }
        });
        ytPlayerView = findViewById(R.id.youtube_player_view);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("beanbean", "onDestroy: ");
        tts.shutdown();
        ytPlayerView.release();
    }

    private void addToFavorite(Intent intent){
        final int wordId = intent.getIntExtra("id", 0);
        final int userId = loginManager.getUserId();
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

                    if(loginManager.isLogin() && Server.haveNetworkConnection(MeaningActivity.this)){
                        final long insertId = databaseAccess.addFavorite(wordId, 0, 0,0);
                        String sendDataUrl = Server.SEND_DATA_URL;
                        final RequestQueue requestQueue = Volley.newRequestQueue(MeaningActivity.this);

                        //Initial request body
                        JSONArray jsonArray = new JSONArray();
                        try {
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("table", "WordLike");
                            JSONArray jsonArray1 = new JSONArray();
                            JSONObject jsonObject1 = new JSONObject();
                            jsonObject1.put("Id", insertId);
                            jsonObject1.put("IdUser", userId);
                            jsonObject1.put("IdWord", wordId);
                            jsonObject1.put("Remembered", 0);
                            jsonObject1.put("Synchronized", 0);
                            jsonObject1.put("IsChange", 0);
                            jsonObject1.put("IdServer", 0);
                            jsonArray1.put(jsonObject1);
                            jsonObject.put("data", jsonArray1);
                            jsonArray.put(jsonObject);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        JsonArrayRequest request = new JsonArrayRequest(Request.Method.POST, sendDataUrl, jsonArray, new Response.Listener<JSONArray>() {
                            @Override
                            public void onResponse(JSONArray response) {
                                if(response.length() > 0){
                                    try {
                                        JSONObject dataArray = (JSONObject) response.get(0);
                                        Log.d(TAG, "onResponse: " + dataArray);
                                        JSONArray array = (JSONArray) dataArray.get("data");
                                        JSONObject data = (JSONObject) array.get(0);
                                        int idServer = data.getInt("IdServer");
                                        databaseAccess.updateFavoriteIdServer(insertId, idServer);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.e("Error: ", error.getMessage() != null ? error.getMessage() : "null pointer");
                            }
                        });
                        requestQueue.add(request);
                    }else{ // No internet or no login add to local
                        databaseAccess.addFavorite(wordId, 0, 0,0);
                    }
                }
            }

            @Override
            public void unLiked(LikeButton likeButton) {
                databaseAccess.removeFavorite(wordId);
            }
        });
    }

    public boolean isLocationEnabled(Context context) {
        int locationMode = 0;
        String locationProviders;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            try {
                locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);

            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
                return false;
            }

            return locationMode != Settings.Secure.LOCATION_MODE_OFF;

        }else{
            locationProviders = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            return !TextUtils.isEmpty(locationProviders);
        }

    }

    public void saveHistoryWithoutLocation(final int wordID, final int pUserID){
        final long currentDateTime = System.currentTimeMillis();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss", getCurrentLocale(this));
        String dateString = simpleDateFormat.format(currentDateTime);
        //Nếu có internet và đã login thì add vô server vào local với sync status = success
        if(Server.haveNetworkConnection(this) && pUserID > 0){
            final long insertId = databaseAccess.addHistory(wordID, currentDateTime, pUserID, 0,0);
            String sendDataUrl = Server.SEND_DATA_URL;
            final RequestQueue requestQueue = Volley.newRequestQueue(this);

            //Initial request body
            JSONArray jsonArray = new JSONArray();
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("table", "SearchHistory");
                JSONArray jsonArray1 = new JSONArray();
                JSONObject jsonObject1 = new JSONObject();
                jsonObject1.put("Id", insertId);
                jsonObject1.put("IdUser", pUserID);
                jsonObject1.put("IdWord", wordID);
                jsonObject1.put("Remembered", 0);
                jsonObject1.put("Synchronized", 0);
                jsonObject1.put("TimeSearch", dateString);
                jsonObject1.put("LinkWeb", "");
                jsonObject1.put("IsChange", 0);
                jsonObject1.put("IdServer", 0);
                jsonArray1.put(jsonObject1);
                jsonObject.put("data", jsonArray1);
                jsonArray.put(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            JsonArrayRequest request = new JsonArrayRequest(Request.Method.POST, sendDataUrl, jsonArray, new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    if(response.length() > 0){
                        try {
                            JSONObject dataArray = (JSONObject) response.get(0);
                            JSONArray array = (JSONArray) dataArray.get("data");
                            JSONObject data = (JSONObject) array.get(0);
                            int idServer = data.getInt("IdServer");
                            databaseAccess.updateHistoryIdServer(insertId, idServer);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if(error != null){
                        Log.e("Error: ", error.getMessage() == null ? "null pointer" : error.getMessage());
                    }

                }
            });
            requestQueue.add(request);
        }else{ //Nếu không có internet hoặc chưa login thì add vô local với sync status = fail
            databaseAccess.addHistory(wordID, System.currentTimeMillis(), 0, 0,0);
        }
    }

    Locale getCurrentLocale(Context context){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            return context.getResources().getConfiguration().getLocales().get(0);
        } else{
            //noinspection deprecation
            return context.getResources().getConfiguration().locale;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void saveHistory(final int wordID, final int pUserID)
    {
        final LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        boolean isLocationEnabled = isLocationEnabled(this);
        if (!isLocationEnabled){
            saveHistoryWithoutLocation(wordID, pUserID);
        } else {
            if (this.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                this.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_LOCATION);
                saveHistoryWithoutLocation(wordID, pUserID);
            } else {
                LocationServices.getFusedLocationProviderClient(this).requestLocationUpdates(locationRequest, new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        super.onLocationResult(locationResult);
                        LocationServices.getFusedLocationProviderClient(MeaningActivity.this).removeLocationUpdates(this);
                        if (locationResult != null && locationResult.getLocations().size() > 0) {
                            int latestLocationIndex = locationResult.getLocations().size() - 1;
                            double latitude = locationResult.getLocations().get(latestLocationIndex).getLatitude();
                            double longtitude = locationResult.getLocations().get(latestLocationIndex).getLongitude();
                            getAddress(latitude, longtitude, wordID, pUserID);
                        }
                    }
                }, Looper.getMainLooper());
            }
        }


    }

    public void getAddress(double lat, double lng, int wordID, int pUserID) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
            Address obj = addresses.get(0);
            String location = obj.getAddressLine(0);
            final long currentDateTime = System.currentTimeMillis();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss", getCurrentLocale(this));
            String dateString = simpleDateFormat.format(currentDateTime);
            //Nếu có internet và đã login thì add vô server vào local với sync status = success
            if(Server.haveNetworkConnection(this) && pUserID > 0){
                final long insertId = databaseAccess.addHistory(wordID, currentDateTime, pUserID, 0,0, location);
                String sendDataUrl = Server.SEND_DATA_URL;
                final RequestQueue requestQueue = Volley.newRequestQueue(this);

                //Initial request body
                JSONArray jsonArray = new JSONArray();
                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("table", "SearchHistory");
                    JSONArray jsonArray1 = new JSONArray();
                    JSONObject jsonObject1 = new JSONObject();
                    jsonObject1.put("Id", insertId);
                    jsonObject1.put("IdUser", pUserID);
                    jsonObject1.put("IdWord", wordID);
                    jsonObject1.put("Remembered", 0);
                    jsonObject1.put("Synchronized", 0);
                    jsonObject1.put("TimeSearch", dateString);
                    jsonObject1.put("LinkWeb", "");
                    jsonObject1.put("IsChange", 0);
                    jsonObject1.put("IdServer", 0);
                    jsonObject1.put("location", location);
                    jsonArray1.put(jsonObject1);
                    jsonObject.put("data", jsonArray1);
                    jsonArray.put(jsonObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                JsonArrayRequest request = new JsonArrayRequest(Request.Method.POST, sendDataUrl, jsonArray, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        if(response.length() > 0){
                            try {
                                JSONObject dataArray = (JSONObject) response.get(0);
                                JSONArray array = (JSONArray) dataArray.get("data");
                                JSONObject data = (JSONObject) array.get(0);
                                int idServer = data.getInt("IdServer");
                                databaseAccess.updateHistoryIdServer(insertId, idServer);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if(error != null){
                            Log.e("Error: ", error.getMessage() == null ? "null pointer" : error.getMessage());
                        }

                    }
                });
                requestQueue.add(request);
            }else{ //Nếu không có internet hoặc chưa login thì add vô local với sync status = fail
                databaseAccess.addHistory(wordID, System.currentTimeMillis(), 0, 0,0, location);
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public long getCurrentTimeInMillis(){
        return System.currentTimeMillis();
    }

    public void showPopup(final int wordId, String word, String description){
        final DatabaseAccess databaseAccess1 = DatabaseAccess.getInstance(this);
        historyDateList = databaseAccess1.getHistoryDateByWordId(wordId);
        remindedDateList = databaseAccess1.getRemindedWordDateById(wordId);
        popupHistoryAdapter = new PopupHistoryAdapter(this, historyDateList);
        popupRemindedAdapter = new PopupRemindedAdapter(this, remindedDateList);
        if(meaningPopup.isShowing()){
            meaningPopup.dismiss();
        }
        TextView popUpWord, popUpDescription, popUpHistory, popUpReminded;
        MaterialButton popUpBtnRemember, popUpBtnNotRemember;
        ListView popUpListViewHistory, popUpListViewReminder;
        meaningPopup.setContentView(R.layout.popup_word);
        popUpHistory = meaningPopup.findViewById(R.id.popup_meaning_txt_history);
        popUpHistory.setText("Lịch sử tra từ" + " (" + historyDateList.size()+")");
        popUpReminded = meaningPopup.findViewById(R.id.popup_meaning_txt_reminded);
        popUpReminded.setText("Lịch sử nhắc nhở" + " (" + remindedDateList.size()+")");
        popUpBtnNotRemember = meaningPopup.findViewById(R.id.popup_btn_not_remember);
        popUpWord = meaningPopup.findViewById(R.id.popup_txt_word);
        popUpListViewHistory = meaningPopup.findViewById(R.id.popup_lv_history);
        popUpListViewHistory.setAdapter(popupHistoryAdapter);
        popUpListViewReminder = meaningPopup.findViewById(R.id.popup_lv_reminder);
        popUpListViewReminder.setAdapter(popupRemindedAdapter);
        popUpWord.setText(word);
        popUpDescription = meaningPopup.findViewById(R.id.popup_txt_description);
        popUpDescription.setText(description);
        popUpBtnRemember = meaningPopup.findViewById(R.id.popup_btn_remember);
        popUpBtnRemember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int alarmId = alarmPropsManager.getAlarmType();
                if(alarmId == DatabaseContract.ALARM_HISTORY){
                    databaseAccess1.setHistoryRememberByWordId(wordId);
                    if(Server.haveNetworkConnection(MeaningActivity.this) && loginManager.isLogin()){
                        syncRememberedHistory(wordId);
                    }
                    if(!isRememberSaved(wordId)){
                        databaseAccess1.addRememberedWord(wordId, getCurrentTimeInMillis());
                    }
                }else if(alarmId == DatabaseContract.ALARM_FAVORITE){
                    databaseAccess1.setFavoriteRememberByWordId(wordId);
                    if(Server.haveNetworkConnection(MeaningActivity.this) && loginManager.isLogin()){
                        syncFavoriteHistory(wordId);
                    }
                    if(!isRememberSaved(wordId)){
                        databaseAccess1.addRememberedWord(wordId, getCurrentTimeInMillis());
                    }
                }else{
                    databaseAccess1.setTopicRemember(wordId, alarmId);
                    if(Server.haveNetworkConnection(MeaningActivity.this) && loginManager.isLogin()){
                        syncTopicRemember(wordId, alarmId);
                    }
                    if(!isRememberSaved(wordId)){
                        databaseAccess1.addRememberedWord(wordId, getCurrentTimeInMillis());
                    }
                }
                meaningPopup.dismiss();
            }
        });

        popUpBtnNotRemember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                meaningPopup.dismiss();
            }
        });
        meaningPopup.show();
    }

    public void syncRememberedHistory(int wordId){
        final RequestQueue requestQueue = Volley.newRequestQueue(this);
        HistoryWord history = databaseAccess.getHistoryByWordId(wordId);
        final long searchTime = history.getSearchTime();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss", getCurrentLocale(this));
        String dateString = simpleDateFormat.format(searchTime);
        //Initial request body
        JSONArray jsonArray = new JSONArray();
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("table", "SearchHistory");
            JSONArray jsonArray1 = new JSONArray();
            JSONObject jsonObject1 = new JSONObject();
            jsonObject1.put("Id", history.getId());
            jsonObject1.put("IdUser", history.getUserId());
            jsonObject1.put("IdWord", history.getWordId());
            jsonObject1.put("Remembered", 1);
            jsonObject1.put("Synchronized", history.getSynchronized());
            jsonObject1.put("TimeSearch", dateString);
            jsonObject1.put("LinkWeb", history.getLinkWeb());
            jsonObject1.put("IsChange", 1);
            jsonObject1.put("IdServer", history.getIdServer());
            jsonArray1.put(jsonObject1);
            jsonObject.put("data", jsonArray1);
            jsonArray.put(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String sendDataUrl = Server.SEND_DATA_URL;
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.POST, sendDataUrl, jsonArray, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                if(response.length() > 0){
                    try {
                        JSONObject dataArray = (JSONObject) response.get(0);
                        Log.d(TAG, "onResponse: " + dataArray);
                        JSONArray array = (JSONArray) dataArray.get("data");
                        JSONObject data = (JSONObject) array.get(0);
                        int id = data.getInt("Id");
                        databaseAccess.updateHistoryIsChange(id);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Error: ", error.getMessage() != null ? error.getMessage() : "null pointer");
            }
        });
        requestQueue.add(request);
    }

    public void syncFavoriteHistory(int wordId){
        final RequestQueue requestQueue = Volley.newRequestQueue(this);
        HistoryWord favorite = databaseAccess.getFavoriteByWordId(wordId);
        //Initial request body
        JSONArray jsonArray = new JSONArray();
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("table", "SearchHistory");
            JSONArray jsonArray1 = new JSONArray();
            JSONObject jsonObject1 = new JSONObject();
            jsonObject1.put("Id", favorite.getId());
            jsonObject1.put("IdUser", favorite.getUserId());
            jsonObject1.put("IdWord", favorite.getWordId());
            jsonObject1.put("Remembered", 1);
            jsonObject1.put("IsChange", 1);
            jsonObject1.put("IdServer", favorite.getIdServer());
            jsonArray1.put(jsonObject1);
            jsonObject.put("data", jsonArray1);
            jsonArray.put(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String sendDataUrl = Server.SEND_DATA_URL;
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.POST, sendDataUrl, jsonArray, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                if(response.length() > 0){
                    try {
                        JSONObject dataArray = (JSONObject) response.get(0);
                        Log.d(TAG, "onResponse: " + dataArray);
                        JSONArray array = (JSONArray) dataArray.get("data");
                        JSONObject data = (JSONObject) array.get(0);
                        int id = data.getInt("Id");
                        databaseAccess.updateFavoriteIsChange(id);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Error: ", error.getMessage() != null ? error.getMessage() : "null pointer");
            }
        });
        requestQueue.add(request);
    }

    public void syncTopicRemember(int wordId, int topicId){
        final RequestQueue requestQueue = Volley.newRequestQueue(this);
        TopicWord topicWord = databaseAccess.getTopicRememberByWordIdAndTopicId(wordId, topicId);
        //Initial request body
        JSONArray jsonArray = new JSONArray();
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("table", "TopicRemember");
            JSONArray jsonArray1 = new JSONArray();
            JSONObject jsonObject1 = new JSONObject();
            jsonObject1.put("Id", topicWord.getId());
            jsonObject1.put("IdUser", loginManager.getUserId());
            jsonObject1.put("IdTopic", topicWord.getTopicId());
            jsonObject1.put("IdWord", topicWord.getWordId());
            jsonObject1.put("IsRemember", 1);
            jsonObject1.put("IsChange", 1);
            jsonObject1.put("IdServer", topicWord.getIdServer());
            jsonArray1.put(jsonObject1);
            jsonObject.put("data", jsonArray1);
            jsonArray.put(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String sendDataUrl = Server.SEND_DATA_URL;
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.POST, sendDataUrl, jsonArray, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                if(response.length() > 0){
                    try {
                        JSONObject dataArray = (JSONObject) response.get(0);
                        Log.d(TAG, "onResponse: " + dataArray);
                        JSONArray array = (JSONArray) dataArray.get("data");
                        JSONObject data = (JSONObject) array.get(0);
                        int id = data.getInt("Id");
                        databaseAccess.updateTopicRememberIsChange(id);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Error: ", error.getMessage() != null ? error.getMessage() : "null pointer");
            }
        });
        requestQueue.add(request);
    }

    private boolean isRememberSaved(int wordId){
        if(databaseAccess.getRememberedWordByWordId(wordId).getId() > 0){
            return true;
        }else{
            return false;
        }
    }

    private void loadingImage(String query){
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, Server.UNSPLASH_SEARCH + query, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray object = response.getJSONArray("results");
                            JSONObject urls = object.getJSONObject(0).getJSONObject("urls");
                            String regular_url = urls.getString("regular");
                            Picasso.get().load(regular_url).into(imgMeaning);
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

    private void hideSoftKeyBoard() {
        View v = this.getWindow().getCurrentFocus();
        if (v != null) {
            InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    }

}
