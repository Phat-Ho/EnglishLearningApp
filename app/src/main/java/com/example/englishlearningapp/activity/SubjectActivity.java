package com.example.englishlearningapp.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.TextView;

import com.example.englishlearningapp.R;
import com.example.englishlearningapp.adapters.SubjectAdapter;
import com.example.englishlearningapp.models.Subject;
import com.example.englishlearningapp.navigation_bottom_fragments.FriendsFragment;
import com.example.englishlearningapp.navigation_bottom_fragments.HomeFragment;
import com.example.englishlearningapp.navigation_bottom_fragments.ProfileFragment;
import com.example.englishlearningapp.navigation_bottom_fragments.SearchFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

public class SubjectActivity extends AppCompatActivity {
    TextView subjectScore;
    TextView subjectLevel;
    RecyclerView subjectRecyclerView;
    ArrayList<Subject> subjectArrayList;
    SubjectAdapter subjectAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subject);
        MappingView();
        InitSubjectRecyclerView();
        GetSubjectData();
    }

    private void GetSubjectData() {
        subjectArrayList.add(new Subject("Bạn bè", "https://www.pngitem.com/pimgs/m/243-2436634_clip-art-two-friend-cupcakes-draw-best-friends.png"));
        subjectArrayList.add(new Subject("Du lịch", "https://previews.123rf.com/images/wowomnom/wowomnom1504/wowomnom150400005/38431289-vector-illustration-of-red-suitcase-and-travel-accessories-on-light-background-hand-draw-line-art-de.jpg"));
        subjectArrayList.add(new Subject("Trường học", "https://www.drawingnow.com/file/videos/image/how-to-draw-a-school.jpg"));
        subjectArrayList.add(new Subject("Marketing", "https://jobsgo.vn/blog/wp-content/uploads/2019/09/nghe-marketing-1.jpg"));
        subjectAdapter.notifyDataSetChanged();
    }

    private void InitSubjectRecyclerView() {
        subjectArrayList = new ArrayList<>();

        //init Adapter
        subjectAdapter = new SubjectAdapter(subjectArrayList);
        subjectRecyclerView.setHasFixedSize(true);

        //Set up layout
        GridLayoutManager layoutManager = new GridLayoutManager(SubjectActivity.this, 2);
        subjectRecyclerView.setLayoutManager(layoutManager);

        //Attach adapter
        subjectRecyclerView.setAdapter(subjectAdapter);
    }

    private void MappingView() {
        subjectLevel = findViewById(R.id.subject_level);
        subjectScore = findViewById(R.id.subject_score);
        subjectRecyclerView = findViewById(R.id.subject_recyclerview);
    }
}
