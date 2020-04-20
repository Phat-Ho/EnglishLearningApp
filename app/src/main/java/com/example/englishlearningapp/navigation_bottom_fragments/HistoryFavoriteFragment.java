package com.example.englishlearningapp.navigation_bottom_fragments;

import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.englishlearningapp.R;
import com.example.englishlearningapp.adapters.HistoryViewPagerAdapter;
import com.example.englishlearningapp.fragments.FavoriteFragment;
import com.example.englishlearningapp.fragments.HistoryFragment;
import com.example.englishlearningapp.fragments.RememberedFragment;
import com.google.android.material.tabs.TabLayout;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HistoryFavoriteFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HistoryFavoriteFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    Toolbar historyFavToolbar;
    TabLayout historyFavTabLayout;
    ViewPager historyFavViewPager;
    HistoryFragment historyFragment = new HistoryFragment();
    FavoriteFragment favoriteFragment = new FavoriteFragment();
    RememberedFragment rememberedFragment = new RememberedFragment();

    public HistoryFavoriteFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HistoryFavoriteFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HistoryFavoriteFragment newInstance(String param1, String param2) {
        HistoryFavoriteFragment fragment = new HistoryFavoriteFragment();
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
        View view  = inflater.inflate(R.layout.fragment_history_favorite, container,  false);
        historyFavTabLayout = view.findViewById(R.id.history_favorite_tabLayout);
        historyFavToolbar = view.findViewById(R.id.history_favorite_toolbar);
        historyFavViewPager = view.findViewById(R.id.history_favorite_viewPager);
        SetUpViewPager(historyFavViewPager);
        historyFavTabLayout.setupWithViewPager(historyFavViewPager);
        return view;
    }

    private void SetUpViewPager(ViewPager viewPager) {
        HistoryViewPagerAdapter pagerAdapter = new HistoryViewPagerAdapter(getChildFragmentManager());
        pagerAdapter.addFragment(historyFragment, "Lịch sử");
        pagerAdapter.addFragment(favoriteFragment, "Yêu thích");
        pagerAdapter.addFragment(rememberedFragment, "Đã nhớ");
        viewPager.setAdapter(pagerAdapter);
    }
}
