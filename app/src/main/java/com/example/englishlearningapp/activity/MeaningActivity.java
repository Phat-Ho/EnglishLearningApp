package com.example.englishlearningapp.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.englishlearningapp.R;
import com.example.englishlearningapp.models.Word;
import com.example.englishlearningapp.utils.DatabaseAccess;
import com.example.englishlearningapp.utils.DatabaseContract;
import com.like.LikeButton;
import com.like.OnLikeListener;

import java.util.ArrayList;
import java.util.Locale;

public class MeaningActivity extends AppCompatActivity {
    private static final String TAG = "MeaningActivity";
    TextView txtMeaning;
    ImageButton imgBtnPronounce, imgBtnSearch;
    TextToSpeech tts;
    LikeButton likeBtn;
    DatabaseAccess databaseAccess;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meaning);
        MappingView();
        SetMeaningData();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if(intent.hasExtra("word")){
            String html = intent.getStringExtra("html");
            final String word = intent.getStringExtra("word");
            final int wordId = intent.getIntExtra("id", 0);
            addToFavorite(intent);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                txtMeaning.setText(Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY));
            } else {
                txtMeaning.setText(Html.fromHtml(html));
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
        String html = intent.getStringExtra("html");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            txtMeaning.setText(Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY));
        } else {
            txtMeaning.setText(Html.fromHtml(html));
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
    }

    private void MappingView() {
        txtMeaning = findViewById(R.id.textViewMeaning);
        imgBtnPronounce = findViewById(R.id.imageButtonPronounce);
        imgBtnSearch = findViewById(R.id.meaning_search_btn);
        likeBtn = findViewById(R.id.LikeButtonHeart);
        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR){
                    tts.setLanguage(Locale.ENGLISH);
                }
            }
        });
        imgBtnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent homeIntent = new Intent(MeaningActivity.this, MainHomeActivity.class);
                startActivity(homeIntent);
            }
        });
    }

    private void addToFavorite(Intent intent){
        final int wordId = intent.getIntExtra("id", 0);

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
                Toast.makeText(MeaningActivity.this, "Added \"" + globalWord + "\" to your Favorite", Toast.LENGTH_SHORT).show();
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
}
