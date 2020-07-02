package com.example.englishlearningapp.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.example.englishlearningapp.R;
import com.example.englishlearningapp.adapters.SubjectAdapter;
import com.example.englishlearningapp.models.Subject;
import com.example.englishlearningapp.models.Topic;
import com.example.englishlearningapp.utils.DatabaseAccess;

import java.util.ArrayList;

public class TopicFragment extends Fragment {

    private static final String TAG = "TopicFragment";
    TextView subjectScore;
    TextView subjectLevel;
    RecyclerView subjectRecyclerView;
    ArrayList<Topic> topicList;
    SubjectAdapter subjectAdapter;
    DatabaseAccess databaseAccess;

    public TopicFragment() {
        //Require an empty constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        databaseAccess = DatabaseAccess.getInstance(getActivity());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_subject, container, false);
        subjectScore = view.findViewById(R.id.subject_score);
        subjectLevel = view.findViewById(R.id.subject_level);
        subjectRecyclerView = view.findViewById(R.id.subject_recyclerview);
        return view;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(subjectRecyclerView != null){
            InitSubjectsRecyclerView();
        }
        else{
            Log.d("SubjectRecyclerView", " == null");
        }
    }

    private void InitSubjectsRecyclerView() {
        topicList = databaseAccess.getAllTopic();
        Log.d(TAG, "topic List size: " + topicList.size());
        //init Adapter
        subjectAdapter = new SubjectAdapter(topicList, getActivity());
        subjectRecyclerView.setHasFixedSize(true);

        //Set up layout
        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 2);
        subjectRecyclerView.setLayoutManager(layoutManager);

        //Attach adapter
        subjectRecyclerView.setAdapter(subjectAdapter);
    }
}
