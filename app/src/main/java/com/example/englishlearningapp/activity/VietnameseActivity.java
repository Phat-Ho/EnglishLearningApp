package com.example.englishlearningapp.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.example.englishlearningapp.R;
import com.example.englishlearningapp.models.Word;
import com.example.englishlearningapp.utils.DatabaseAccess;

import java.util.ArrayList;

public class VietnameseActivity extends AppCompatActivity {
    Toolbar vnToolbar;
    EditText vnEdtSearch;
    ListView vnLvWords;
    ArrayList<Word> completeWordsData;
    DatabaseAccess databaseAccess;
    ArrayAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vietnamese);
        databaseAccess = DatabaseAccess.getInstance(this);
        databaseAccess.open();
        MappingView();
        SetUpToolbar();
        loadDatabase(vnEdtSearch.getText().toString().trim());
        vnEdtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                loadDatabase(vnEdtSearch.getText().toString().trim());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        vnLvWords.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String html = completeWordsData.get(position).getHtml();
                Intent intent = new Intent(VietnameseActivity.this, VietnameseMeaningActivity.class);
                intent.putExtra("html", html);
                startActivity(intent);
            }
        });
    }

    private void loadDatabase(String word) {
        if (word.equals("")) {
            completeWordsData = databaseAccess.getVietnameseWords(word);
            adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, completeWordsData);
            vnLvWords.setAdapter(adapter);
        } else {
            completeWordsData = databaseAccess.getVietnameseWords(word);
            adapter.clear();
            adapter.addAll(completeWordsData);
            adapter.notifyDataSetChanged();
        }
    }

    private void SetUpToolbar() {
        setSupportActionBar(vnToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        vnToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void MappingView() {
        vnToolbar = findViewById(R.id.vn_toolbar);
        vnEdtSearch = findViewById(R.id.vn_edt_search);
        vnLvWords = findViewById(R.id.vn_lv_words);
    }
}
