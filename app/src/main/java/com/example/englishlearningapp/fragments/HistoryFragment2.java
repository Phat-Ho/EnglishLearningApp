package com.example.englishlearningapp.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.englishlearningapp.R;
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
    ArrayAdapter<Word> arrayAdapter;
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

    public void sortItemListView(int sortType){
        if(sortType == SharedPrefsManager.BY_ALPHABET){
            wordList.clear();
            wordList.addAll(databaseAccess.getHistoryWordsWithDuplicateSortByAZ());
            arrayAdapter.notifyDataSetChanged();
        }else{
            wordList.clear();
            wordList.addAll(databaseAccess.getHistoryWordsWithDuplicateSortByTimeLatest());
            arrayAdapter.notifyDataSetChanged();
        }
    }

    private void SetUpListView() {
        if(prefsManager.getSortBy() == SharedPrefsManager.BY_ALPHABET){
            wordList.clear();
            wordList.addAll(databaseAccess.getHistoryWordsWithDuplicateSortByAZ());
        }else{
            wordList.clear();
            wordList.addAll(databaseAccess.getHistoryWordsWithDuplicateSortByTimeLatest());
        }
        arrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, wordList);
        historyFrag2ListView.setAdapter(arrayAdapter);
    }
}
