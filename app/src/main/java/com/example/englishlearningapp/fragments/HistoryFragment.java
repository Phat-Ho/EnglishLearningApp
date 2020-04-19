package com.example.englishlearningapp.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.englishlearningapp.R;
import com.example.englishlearningapp.activity.HistoryActivity;
import com.example.englishlearningapp.activity.MeaningActivity;
import com.example.englishlearningapp.models.Word;
import com.example.englishlearningapp.utils.DatabaseAccess;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class HistoryFragment extends Fragment {
    private static final int MEANING_CODE = 1;
    ListView historyFragmentListView;
    ArrayList<Word> wordList;
    ArrayAdapter arrayAdapter;
    DatabaseAccess databaseAccess;

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
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == MEANING_CODE){
            if(resultCode == Activity.RESULT_OK){
                wordList = databaseAccess.getHistoryWords();
                arrayAdapter.notifyDataSetChanged();
            }
        }
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

        historyFragmentListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                ShowAlert(getActivity(), "Xóa lịch sử", "Bạn có muốn xóa lịch sử?", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int id = wordList.get(position).getId();
                        databaseAccess.open();
                        if(databaseAccess.removeHistory(id)>0){
                            wordList.remove(position);
                            arrayAdapter.notifyDataSetChanged();
                        }
                    }
                }, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                return true;
            }
        });
    }

    private void ShowAlert(Context context, String alertTitle, String alertMessage
                                , DialogInterface.OnClickListener onPositiveClick
                                , DialogInterface.OnClickListener onNegativeClick){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(alertTitle);
        builder.setMessage(alertMessage);
        builder.setPositiveButton("Xác nhận", onPositiveClick);
        builder.setNegativeButton("Hủy", onNegativeClick);
        builder.show();
    }

    private void LoadHistoryData() {
        databaseAccess.open();
        wordList = databaseAccess.getHistoryWords();
        arrayAdapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, wordList);
        historyFragmentListView.setAdapter(arrayAdapter);
    }

    private void moveToMeaningActivity(String wordHtml, String html, String word, int wordId, int remembered) {
        Intent meaningIntent = new Intent(getActivity(), MeaningActivity.class);
        meaningIntent.putExtra("wordHtml", wordHtml);
        meaningIntent.putExtra("html", html);
        meaningIntent.putExtra("word", word);
        meaningIntent.putExtra("id", wordId);
        meaningIntent.putExtra("remembered", remembered);
        startActivityForResult(meaningIntent, HistoryFragment.MEANING_CODE);
    }
}
