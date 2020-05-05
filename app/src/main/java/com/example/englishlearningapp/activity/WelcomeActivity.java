package com.example.englishlearningapp.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.englishlearningapp.R;
import com.example.englishlearningapp.models.Choice;
import com.example.englishlearningapp.models.Question;
import com.example.englishlearningapp.models.Topic;
import com.example.englishlearningapp.models.Word;
import com.example.englishlearningapp.utils.DatabaseAccess;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.Random;

public class WelcomeActivity extends AppCompatActivity {
    private static final String TAG = "WelcomeActivity";
    TextView welcomeTxtQuestDetail, welcomeTxtTopic,
            welcomeTxtMeaning1, welcomeTxtMeaning2,
            welcomeTxtMeaning3, welcomeTxtMeaning4;
    MaterialButton welcomeBtn1, welcomeBtn2,
                    welcomeBtn3, welcomeBtn4, welcomeBtnNext;
    DatabaseAccess databaseAccess;
    ArrayList<Choice> choiceList = null;
    ArrayList<Question> questionList = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        MappingView();
        databaseAccess = DatabaseAccess.getInstance(this);
        SetUpView();
        HandleEvent();
    }

    private void SetUpView() {
        questionList = databaseAccess.getAllQuestion();
        if(questionList.size() > 0){
            int index = randomIndex(questionList.size());
            welcomeTxtQuestDetail.setText(questionList.get(index).getQuestionDetail());
            Topic topic = databaseAccess.getTopicByQuestionId(questionList.get(index).getQuestionId());
            welcomeTxtTopic.setText("(Topic " + topic.getTopicName() + ")");
            choiceList = databaseAccess.getChoicesByQuestionId(questionList.get(index).getQuestionId());
            if(choiceList.size() > 3){
                Word word = new Word();
                word = databaseAccess.getWordsById(choiceList.get(0).getWordId());
                welcomeBtn1.setText(word.getWord());
                welcomeTxtMeaning1.setText(getMeaning(word.getDescription()));

                word = databaseAccess.getWordsById(choiceList.get(1).getWordId());
                welcomeBtn2.setText(word.getWord());
                welcomeTxtMeaning2.setText(getMeaning(word.getDescription()));

                word = databaseAccess.getWordsById(choiceList.get(2).getWordId());
                welcomeBtn3.setText(databaseAccess.getWordsById(choiceList.get(2).getWordId()).getWord());
                welcomeTxtMeaning3.setText(getMeaning(word.getDescription()));

                word = databaseAccess.getWordsById(choiceList.get(3).getWordId());
                welcomeBtn4.setText(word.getWord());
                welcomeTxtMeaning4.setText(getMeaning(word.getDescription()));
            }
        }else{
            Log.d(TAG, "SetUpView: question empty");
        }


    }

    private void MappingView() {
        welcomeBtn1 = findViewById(R.id.welcome_btn_1);
        welcomeBtn2 = findViewById(R.id.welcome_btn_2);
        welcomeBtn3 = findViewById(R.id.welcome_btn_3);
        welcomeBtn4 = findViewById(R.id.welcome_btn_4);
        welcomeBtnNext = findViewById(R.id.welcome_btn_next);
        welcomeTxtMeaning1 = findViewById(R.id.welcome_txt_meaning_1);
        welcomeTxtMeaning2 = findViewById(R.id.welcome_txt_meaning_2);
        welcomeTxtMeaning3 = findViewById(R.id.welcome_txt_meaning_3);
        welcomeTxtMeaning4 = findViewById(R.id.welcome_txt_meaning_4);
        welcomeTxtQuestDetail = findViewById(R.id.welcome_txt_quest_detail);
        welcomeTxtTopic = findViewById(R.id.welcome_txt_topic);
    }

    private void HandleEvent(){
        welcomeBtnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        welcomeBtn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(choiceList.get(0).isRight() == 1){
                    welcomeBtn1.setBackgroundColor(getResources().getColor(R.color.colorGreen));
                    showMeaning(1);
                    disableButton();
                }else{
                    welcomeBtn1.setBackgroundColor(getResources().getColor(R.color.colorRed));
                    showAllMeaning();
                    disableButton();
                }
            }
        });

        welcomeBtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(choiceList.get(1).isRight() == 1){
                    welcomeBtn2.setBackgroundColor(getResources().getColor(R.color.colorGreen));
                    showMeaning(2);
                    disableButton();
                }else{
                    welcomeBtn2.setBackgroundColor(getResources().getColor(R.color.colorRed));
                    showAllMeaning();
                    disableButton();
                }
            }
        });

        welcomeBtn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(choiceList.get(2).isRight() == 1){
                    welcomeBtn3.setBackgroundColor(getResources().getColor(R.color.colorGreen));
                    showMeaning(3);
                    disableButton();
                }else{
                    welcomeBtn3.setBackgroundColor(getResources().getColor(R.color.colorRed));
                    showAllMeaning();
                    disableButton();
                }
            }
        });

        welcomeBtn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(choiceList.get(3).isRight() == 1){
                    welcomeBtn4.setBackgroundColor(getResources().getColor(R.color.colorGreen));
                    showMeaning(4);
                    disableButton();
                }else{
                    welcomeBtn4.setBackgroundColor(getResources().getColor(R.color.colorRed));
                    showAllMeaning();
                    disableButton();
                }
            }
        });
    }

    private void showMeaning(int index){
        switch (index){
            case 1:
            {
                welcomeTxtMeaning1.setVisibility(View.INVISIBLE);
                welcomeTxtMeaning2.setVisibility(View.VISIBLE);
                welcomeTxtMeaning3.setVisibility(View.VISIBLE);
                welcomeTxtMeaning4.setVisibility(View.VISIBLE);
                break;
            }
            case 2:
            {
                welcomeTxtMeaning2.setVisibility(View.INVISIBLE);
                welcomeTxtMeaning1.setVisibility(View.VISIBLE);
                welcomeTxtMeaning3.setVisibility(View.VISIBLE);
                welcomeTxtMeaning4.setVisibility(View.VISIBLE);
                break;
            }
            case 3:
            {
                welcomeTxtMeaning3.setVisibility(View.INVISIBLE);
                welcomeTxtMeaning2.setVisibility(View.VISIBLE);
                welcomeTxtMeaning1.setVisibility(View.VISIBLE);
                welcomeTxtMeaning4.setVisibility(View.VISIBLE);
                break;
            }
            case 4:
            {
                welcomeTxtMeaning4.setVisibility(View.INVISIBLE);
                welcomeTxtMeaning2.setVisibility(View.VISIBLE);
                welcomeTxtMeaning3.setVisibility(View.VISIBLE);
                welcomeTxtMeaning1.setVisibility(View.VISIBLE);
                break;
            }
        }
    }

    private void showAllMeaning(){
        welcomeTxtMeaning1.setVisibility(View.VISIBLE);
        welcomeTxtMeaning2.setVisibility(View.VISIBLE);
        welcomeTxtMeaning3.setVisibility(View.VISIBLE);
        welcomeTxtMeaning4.setVisibility(View.VISIBLE);
    }

    private void disableButton(){
        welcomeBtn1.setEnabled(false);
        welcomeBtn2.setEnabled(false);
        welcomeBtn3.setEnabled(false);
        welcomeBtn4.setEnabled(false);
    }

    private int randomIndex(int arraySize){
        Random randomNumGenerator = new Random();
        int temp = randomNumGenerator.nextInt(arraySize);
        return temp;
    }

    private String getMeaning(String str){
        String meaning = str;
        if(str.contains(")")){
            if(str.indexOf(",", str.indexOf(")")) != -1){
                meaning = str.substring(str.indexOf(")") + 2, str.indexOf(",", str.indexOf(")")));
            }else if(str.indexOf(";", str.indexOf(")")) != -1){
                meaning = str.substring(str.indexOf(")") + 2, str.indexOf(";", str.indexOf(")")));
            }else{
                meaning = str.substring(str.lastIndexOf(")") + 2);
            }
        }else if(str.contains(":")){
            if(str.indexOf(",", str.indexOf(":")) != -1){
                meaning = str.substring(str.indexOf(":") + 2, str.indexOf(",", str.indexOf(":")));
            }else if(str.indexOf(";", str.indexOf(":")) != -1){
                meaning = str.substring(str.indexOf(":") + 2, str.indexOf(";", str.indexOf(":")));
            }else{
                meaning = str.substring(str.lastIndexOf(":") + 2);
            }
        }else {
            if(str.indexOf(",", str.indexOf(",")) != -1){
                meaning = str.substring(str.indexOf(",") + 2, str.indexOf(",", str.indexOf(",")));
            }else{
                meaning = str.substring(str.lastIndexOf(",") + 2);
            }
        }

        return meaning;
    }
}
