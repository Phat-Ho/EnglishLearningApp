package com.example.englishlearningapp.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.englishlearningapp.R;
import com.example.englishlearningapp.activity.MeaningActivity;
import com.example.englishlearningapp.models.Word;
import com.example.englishlearningapp.utils.DatabaseAccess;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class FavoriteFragment extends Fragment {

    ListView lvFavorite;
    ArrayList<Word> wordList;
    ArrayAdapter adapter;
    DatabaseAccess databaseAccess;

    public FavoriteFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_favorite, container, false);
        lvFavorite = view.findViewById(R.id.listViewFavorite);
        databaseAccess = DatabaseAccess.getInstance(getActivity());
        return view;
    }

    private void loadFavoriteData(){
        databaseAccess.open();
        wordList = databaseAccess.getFavoriteWords();
        adapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, wordList);
        lvFavorite.setAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        loadFavoriteData();
        lvFavorite.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String t = wordList.get(position).getHtml();
                int start = t.indexOf("<h1>");
                int end = t.indexOf("<h2>");
                String replacement = "";
                String toBeReplaced = t.substring(start, end);
                String wordHtml = toBeReplaced;
                String meaningHtml = t.replace(toBeReplaced, replacement);

                String word = wordList.get(position).getWord();
                int wordId = wordList.get(position).getId();
                int remembered = wordList.get(position).getRemembered();
                moveToMeaningActivity(wordHtml, meaningHtml, word, wordId, remembered);
            }
        });
    }

    private void moveToMeaningActivity(String wordHtml, String html, String word, int wordId, int remembered) {
        Intent meaningIntent = new Intent(getActivity(), MeaningActivity.class);
        meaningIntent.putExtra("wordHtml", wordHtml);
        meaningIntent.putExtra("html", html);
        meaningIntent.putExtra("word", word);
        meaningIntent.putExtra("id", wordId);
        meaningIntent.putExtra("remembered", remembered);
        startActivity(meaningIntent);
    }
}
