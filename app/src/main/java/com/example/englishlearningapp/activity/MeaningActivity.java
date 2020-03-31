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

import java.util.Locale;

public class MeaningActivity extends AppCompatActivity {
    private static final String TAG = "MeaningActivity";
    TextView txtMeaning;
    ImageButton imgBtnPronounce;
    TextToSpeech tts;

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



    private void SetMeaningData() {
        Intent intent = getIntent();
        String html = intent.getStringExtra("html");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            txtMeaning.setText(Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY));
        } else {
            txtMeaning.setText(Html.fromHtml(html));
        }

        final String word = intent.getStringExtra("word");
        imgBtnPronounce.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tts.speak(word, TextToSpeech.QUEUE_FLUSH, null, "");
            }
        });
    }

    private void MappingView() {
        txtMeaning = findViewById(R.id.textViewMeaning);
        imgBtnPronounce = findViewById(R.id.imageButtonPronounce);
        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR){
                    tts.setLanguage(Locale.ENGLISH);
                }
            }
        });
    }
}
