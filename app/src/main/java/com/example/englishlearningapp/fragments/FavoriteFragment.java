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
                String word = wordList.get(position).getWord();
                String html = wordList.get(position).getHtml();
                int wordId = wordList.get(position).getId();
                moveToMeaningActivity(html, word, wordId);
            }
        });
    }

    private void moveToMeaningActivity(String html, String word, int wordId) {
        Intent meaningIntent = new Intent(getActivity(), MeaningActivity.class);
        meaningIntent.putExtra("html", html);
        meaningIntent.putExtra("word", word);
        meaningIntent.putExtra("id", wordId);
        startActivity(meaningIntent);
    }
}
