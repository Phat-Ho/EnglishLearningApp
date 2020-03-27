package com.example.englishlearningapp.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
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
public class HistoryFragment extends Fragment {
    ListView historyFragmentListView;
    ArrayList<Word> wordList;
    ArrayAdapter arrayAdapter;
    DatabaseAccess databaseAccess;
    ArrayList<Word> historyWords;

    public HistoryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_history, container, false);
        historyFragmentListView = view.findViewById(R.id.lv_fragment_history);
        databaseAccess = DatabaseAccess.getInstance(getActivity());
        LoadHistoryData();
        return view;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        HandleEvent();
    }

    private void HandleEvent() {
        historyFragmentListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String html = wordList.get(position).getHtml();
                String word = wordList.get(position).getWord();
                moveToMeaningActivity(html, word);
            }
        });
    }

    private void LoadHistoryData() {
        databaseAccess.open();
        wordList = databaseAccess.getHistoryWords();
        arrayAdapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, wordList);
        historyFragmentListView.setAdapter(arrayAdapter);
    }

    private void moveToMeaningActivity(String html, String word) {
        Intent meaningIntent = new Intent(getActivity(), MeaningActivity.class);
        meaningIntent.putExtra("html", html);
        meaningIntent.putExtra("word", word);
        startActivity(meaningIntent);
    }
}
