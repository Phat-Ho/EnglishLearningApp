package com.example.englishlearningapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.media.Image;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.englishlearningapp.activity.LoginActivity;
import com.google.android.material.button.MaterialButton;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    MaterialButton nextButton, btnLogin;
    Spinner spinnerLang;
    ImageView imgLogo;
    String[] languages = new String[]{"Tiếng Việt", "English"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MappingView();

        imgLogo.setImageResource(R.mipmap.ic_launcher);

        ArrayAdapter adapter = new ArrayAdapter(MainActivity.this, android.R.layout.simple_list_item_1, languages);
        spinnerLang.setAdapter(adapter);

        spinnerLang.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0){
                    setLocale("vi");
                    btnLogin.setText("Đăng nhập");
                } else {
                    setLocale("en");
                    btnLogin.setText("Sign in");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MainHomeActivity.class);
                startActivity(intent);
                finish();
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(loginIntent);
                finish();
            }
        });
    }

    public void setLocale(String localeCode){
        Locale myLocale = new Locale(localeCode);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
    }

    private void MappingView() {
        nextButton = findViewById(R.id.home_next_button);
        spinnerLang = (Spinner) findViewById(R.id.spinner_language);
        imgLogo = (ImageView) findViewById(R.id.imageViewLogo);
        btnLogin = findViewById(R.id.home_login_button);
    }
}
