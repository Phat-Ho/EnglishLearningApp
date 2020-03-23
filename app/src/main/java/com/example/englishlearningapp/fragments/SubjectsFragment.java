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

import com.example.englishlearningapp.R;
import com.example.englishlearningapp.adapters.SubjectAdapter;
import com.example.englishlearningapp.models.Subject;

import java.util.ArrayList;

public class SubjectsFragment extends Fragment {

    TextView subjectScore;
    TextView subjectLevel;
    RecyclerView subjectRecyclerView;
    ArrayList<Subject> subjectArrayList;
    SubjectAdapter subjectAdapter;

    public SubjectsFragment() {
        //Require an empty constructor
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
            GetSubjectData();
        }
        else{
            Log.d("SubjectRecyclerView", " == null");
        }
    }

    private void GetSubjectData() {
        subjectArrayList.add(new Subject("Bạn bè", "https://www.pngitem.com/pimgs/m/243-2436634_clip-art-two-friend-cupcakes-draw-best-friends.png"));
        subjectArrayList.add(new Subject("Du lịch", "https://previews.123rf.com/images/wowomnom/wowomnom1504/wowomnom150400005/38431289-vector-illustration-of-red-suitcase-and-travel-accessories-on-light-background-hand-draw-line-art-de.jpg"));
        subjectArrayList.add(new Subject("Trường học", "https://www.drawingnow.com/file/videos/image/how-to-draw-a-school.jpg"));
        subjectArrayList.add(new Subject("Marketing", "https://jobsgo.vn/blog/wp-content/uploads/2019/09/nghe-marketing-1.jpg"));
        subjectAdapter.notifyDataSetChanged();
    }

    private void InitSubjectsRecyclerView() {
        subjectArrayList = new ArrayList<>();

        //init Adapter
        subjectAdapter = new SubjectAdapter(subjectArrayList);
        subjectRecyclerView.setHasFixedSize(true);

        //Set up layout
        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 2);
        subjectRecyclerView.setLayoutManager(layoutManager);

        //Attach adapter
        subjectRecyclerView.setAdapter(subjectAdapter);
    }
}
