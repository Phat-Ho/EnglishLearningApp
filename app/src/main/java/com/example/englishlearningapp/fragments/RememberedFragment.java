package com.example.englishlearningapp.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.englishlearningapp.R;
import com.example.englishlearningapp.adapters.FavoriteAdapter;
import com.example.englishlearningapp.models.Word;
import com.example.englishlearningapp.utils.DatabaseAccess;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RememberedFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RememberedFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public RememberedFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RemeberedFragment.
     */
    // TODO: Rename and change types and number of parameters

    ListView lvRemembered;
    ArrayList<Word> rememberedWords;
    FavoriteAdapter adapter;
    DatabaseAccess databaseAccess;

    public static RememberedFragment newInstance(String param1, String param2) {
        RememberedFragment fragment = new RememberedFragment();
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_remembered, container, false);
        lvRemembered = view.findViewById(R.id.listViewRemembered);
        databaseAccess = DatabaseAccess.getInstance(getActivity());
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadRememberedData();
    }

    private void loadRememberedData(){
        rememberedWords = databaseAccess.getAllRememberedWords();
        adapter = new FavoriteAdapter(getActivity(), R.layout.row_lv_favorite, rememberedWords);
        lvRemembered.setAdapter(adapter);
    }
}
