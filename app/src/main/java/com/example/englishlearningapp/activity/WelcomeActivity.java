package com.example.englishlearningapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.englishlearningapp.R;
import com.example.englishlearningapp.models.Choice;
import com.example.englishlearningapp.models.Question;
import com.example.englishlearningapp.models.Word;
import com.example.englishlearningapp.utils.DatabaseAccess;
import com.example.englishlearningapp.utils.GlobalVariable;
import com.example.englishlearningapp.utils.LoginManager;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.Random;

public class WelcomeActivity extends AppCompatActivity {
    private static final String TAG = "WelcomeActivity";
    TextView welcomeTxtQuestDetail,
            welcomeTxtMeaning1, welcomeTxtMeaning2,
            welcomeTxtMeaning3, welcomeTxtMeaning4;
    MaterialButton welcomeBtn1, welcomeBtn2,
                    welcomeBtn3, welcomeBtn4, welcomeBtnNext;
    DatabaseAccess databaseAccess;
    ArrayList<Choice> choiceList = null;
    ArrayList<Question> questionList = null;
    LoginManager loginManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GlobalVariable.changeStatusBarColor(WelcomeActivity.this);
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
            choiceList = databaseAccess.getChoicesByQuestionId(questionList.get(index).getQuestionId());
            if(choiceList.size() > 3){
                Word word = null;
                word = databaseAccess.getWordsById(choiceList.get(0).getWordId());
                if(word.getWord().length() > 6){
                    welcomeBtn1.setTextSize(12);
                }
                welcomeBtn1.setText(word.getWord());
                welcomeTxtMeaning1.setText(getMeaning(word.getDescription()));

                word = databaseAccess.getWordsById(choiceList.get(1).getWordId());
                if(word.getWord().length() > 6){
                    welcomeBtn2.setTextSize(12);
                }
                welcomeBtn2.setText(word.getWord());
                welcomeTxtMeaning2.setText(getMeaning(word.getDescription()));

                word = databaseAccess.getWordsById(choiceList.get(2).getWordId());
                if(word.getWord().length() > 6){
                    welcomeBtn3.setTextSize(12);
                }
                welcomeBtn3.setText(databaseAccess.getWordsById(choiceList.get(2).getWordId()).getWord());
                welcomeTxtMeaning3.setText(getMeaning(word.getDescription()));

                word = databaseAccess.getWordsById(choiceList.get(3).getWordId());
                if(word.getWord().length() > 6){
                    welcomeBtn4.setTextSize(12);
                }
                welcomeBtn4.setText(word.getWord());
                welcomeTxtMeaning4.setText(getMeaning(word.getDescription()));
            }
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
        loginManager = new LoginManager(WelcomeActivity.this);
    }

    private void HandleEvent(){
        welcomeBtnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (loginManager.isLogin() == true){
                    Intent intent = new Intent(WelcomeActivity.this, MainHomeActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });

        welcomeBtn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(choiceList.get(0).isRight() == 1){
                    welcomeBtn1.setBackgroundColor(getResources().getColor(R.color.colorGreen));
                    showAllMeaning();
                    disableButton();
                }else{
                    welcomeBtn1.setBackgroundColor(getResources().getColor(R.color.colorRed));
                    for (int i = 0; i < choiceList.size(); i++) {
                        if(choiceList.get(i).isRight() == 1){
                            switch (i){
                                case 1: welcomeBtn2.setBackgroundColor(getResources().getColor(R.color.colorGreen)); break;
                                case 2: welcomeBtn3.setBackgroundColor(getResources().getColor(R.color.colorGreen)); break;
                                case 3: welcomeBtn4.setBackgroundColor(getResources().getColor(R.color.colorGreen)); break;
                            }
                        }
                    }
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
                    showAllMeaning();
                    disableButton();
                }else{
                    welcomeBtn2.setBackgroundColor(getResources().getColor(R.color.colorRed));
                    for (int i = 0; i < choiceList.size(); i++) {
                        if(choiceList.get(i).isRight() == 1){
                            switch (i){
                                case 0: welcomeBtn1.setBackgroundColor(getResources().getColor(R.color.colorGreen)); break;
                                case 2: welcomeBtn3.setBackgroundColor(getResources().getColor(R.color.colorGreen)); break;
                                case 3: welcomeBtn4.setBackgroundColor(getResources().getColor(R.color.colorGreen)); break;
                            }
                        }
                    }
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
                    showAllMeaning();
                    disableButton();
                }else{
                    welcomeBtn3.setBackgroundColor(getResources().getColor(R.color.colorRed));
                    for (int i = 0; i < choiceList.size(); i++) {
                        if(choiceList.get(i).isRight() == 1){
                            switch (i){
                                case 0: welcomeBtn1.setBackgroundColor(getResources().getColor(R.color.colorGreen)); break;
                                case 1: welcomeBtn2.setBackgroundColor(getResources().getColor(R.color.colorGreen)); break;
                                case 3: welcomeBtn4.setBackgroundColor(getResources().getColor(R.color.colorGreen)); break;
                            }
                        }
                    }
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
                    showAllMeaning();
                    disableButton();
                }else{
                    welcomeBtn4.setBackgroundColor(getResources().getColor(R.color.colorRed));
                    for (int i = 0; i < choiceList.size(); i++) {
                        if(choiceList.get(i).isRight() == 1){
                            switch (i){
                                case 0: welcomeBtn1.setBackgroundColor(getResources().getColor(R.color.colorGreen)); break;
                                case 1: welcomeBtn2.setBackgroundColor(getResources().getColor(R.color.colorGreen)); break;
                                case 2: welcomeBtn3.setBackgroundColor(getResources().getColor(R.color.colorGreen)); break;
                            }
                        }
                    }
                    showAllMeaning();
                    disableButton();
                }
            }
        });
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
        welcomeBtnNext.setVisibility(View.VISIBLE);
    }

    private int randomIndex(int arraySize){
        Random randomNumGenerator = new Random();
        int temp = randomNumGenerator.nextInt(arraySize);
        return temp;
    }

    private String getMeaning(String str){
        String meaning;
        if(str.contains(")")){
            if(str.indexOf(",", str.indexOf(")")) != -1){
                meaning = str.substring(str.indexOf(")") + 2, str.indexOf(",", str.indexOf(")")));
            }else if(str.indexOf(";", str.indexOf(")")) != -1){
                meaning = str.substring(str.indexOf(")") + 2, str.indexOf(";", str.indexOf(")")));
            }else{
                meaning = str.substring(str.lastIndexOf(")") + 1);
            }
        }else if(str.contains(":")){
            if(str.indexOf(",", str.indexOf(":")) != -1){
                meaning = str.substring(str.indexOf(":") + 2, str.indexOf(",", str.indexOf(":")));
            }else if(str.indexOf(";", str.indexOf(":")) != -1){
                meaning = str.substring(str.indexOf(":") + 2, str.indexOf(";", str.indexOf(":")));
            }else{
                meaning = str.substring(str.lastIndexOf(":") + 1);
            }
        }else {
            if(str.indexOf(",", str.indexOf(",")) != -1){
                meaning = str.substring(str.indexOf(",") + 2, str.indexOf(",", str.indexOf(",")));
            }else{
                meaning = str.substring(str.lastIndexOf(",") + 1);
            }
        }

        return meaning;
    }
}
