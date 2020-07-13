package com.example.englishlearningapp.fragments;

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

import com.example.englishlearningapp.R;
import com.example.englishlearningapp.activity.MeaningActivity;
import com.example.englishlearningapp.adapters.HistoryAdapter2;
import com.example.englishlearningapp.models.Word;
import com.example.englishlearningapp.utils.DatabaseAccess;
import com.example.englishlearningapp.utils.SharedPrefsManager;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HistoryFragment2#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HistoryFragment2 extends Fragment {
    ListView historyFrag2ListView;
    ArrayList<Word> wordList = new ArrayList<>();
    DatabaseAccess databaseAccess;
    HistoryAdapter2 adapter;
    SharedPrefsManager prefsManager;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HistoryFragment2() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HistoryFragment2.
     */
    // TODO: Rename and change types and number of parameters
    public static HistoryFragment2 newInstance(String param1, String param2) {
        HistoryFragment2 fragment = new HistoryFragment2();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        databaseAccess = DatabaseAccess.getInstance(getActivity());
        prefsManager = new SharedPrefsManager(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_history2, container, false);
        historyFrag2ListView = view.findViewById(R.id.lv_fragment_history_2);
        SetUpListView();
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        HandleEvent();
    }

    private void HandleEvent() {
        historyFrag2ListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String html = wordList.get(position).getHtml();
                String word = wordList.get(position).getWord();
                int wordId = wordList.get(position).getId();
                int remembered = wordList.get(position).getRemembered();
                moveToMeaningActivity(html, word, wordId, remembered);
            }
        });

        historyFrag2ListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                ShowAlert(getActivity(), "Xóa lịch sử", "Bạn có muốn xóa lịch sử?", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int id = wordList.get(position).getId();
                        if(databaseAccess.removeHistory(id)>0){
                            wordList.remove(position);
                            adapter.notifyDataSetChanged();
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

    public void sortItemListView(int sortType){
        if(databaseAccess != null){
            if(sortType == SharedPrefsManager.BY_ALPHABET){
                wordList.clear();
                wordList.addAll(databaseAccess.getHistoryWordsWithDuplicateSortByAZ());
                adapter.notifyDataSetChanged();
            }else{
                wordList.clear();
                wordList.addAll(databaseAccess.getHistoryWordsWithDuplicateSortByTimeLatest());
                adapter.notifyDataSetChanged();
            }
        }
    }

    private void moveToMeaningActivity(String html, String word, int wordId, int remembered) {
        Intent meaningIntent = new Intent(getActivity(), MeaningActivity.class);
        meaningIntent.putExtra("html", html);
        meaningIntent.putExtra("word", word);
        meaningIntent.putExtra("id", wordId);
        meaningIntent.putExtra("remembered", remembered);
        startActivityForResult(meaningIntent, HistoryFragment.MEANING_CODE);
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

    private void SetUpListView() {
        if(prefsManager.getSortBy() == SharedPrefsManager.BY_ALPHABET){
            wordList.clear();
            wordList.addAll(databaseAccess.getHistoryWordsWithDuplicateSortByAZ());
        }else{
            wordList.clear();
            wordList.addAll(databaseAccess.getHistoryWordsWithDuplicateSortByTimeLatest());
        }
        adapter = new HistoryAdapter2(getActivity(), R.layout.row_lv_history_2, wordList);
        historyFrag2ListView.setAdapter(adapter);
    }
}
