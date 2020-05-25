package com.example.englishlearningapp.navigation_bottom_fragments;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.example.englishlearningapp.R;
import com.example.englishlearningapp.adapters.HistoryViewPagerAdapter;
import com.example.englishlearningapp.fragments.FavoriteFragment;
import com.example.englishlearningapp.fragments.HistoryFragment;
import com.example.englishlearningapp.fragments.HistoryFragment2;
import com.example.englishlearningapp.fragments.RememberedFragment;
import com.example.englishlearningapp.utils.SharedPrefsManager;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

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
    Spinner historyFavSpinnerSort, historyFavSpinnerType;
    HistoryFragment historyFragment = new HistoryFragment();
    HistoryFragment2 historyFragment2 = new HistoryFragment2();
    FavoriteFragment favoriteFragment = new FavoriteFragment();
    RememberedFragment rememberedFragment = new RememberedFragment();
    SharedPrefsManager prefsManager;
    HistoryViewPagerAdapter pagerAdapter;

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
        prefsManager = new SharedPrefsManager(getActivity());
        pagerAdapter = new HistoryViewPagerAdapter(getChildFragmentManager());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view  = inflater.inflate(R.layout.fragment_history_favorite, container,  false);
        historyFavTabLayout = view.findViewById(R.id.history_favorite_tabLayout);
        historyFavToolbar = view.findViewById(R.id.history_favorite_toolbar);
        historyFavViewPager = view.findViewById(R.id.history_favorite_viewPager);
        historyFavSpinnerSort = view.findViewById(R.id.history_favorite_spinner_date);
        historyFavSpinnerType = view.findViewById(R.id.history_favorite_spinner_type);
        SetUpSpinner();
        SetUpViewPager(historyFavViewPager);
        historyFavTabLayout.setupWithViewPager(historyFavViewPager);
        HandleSpinnerEvent();
        return view;
    }

    private void SetUpSpinner() {
        String[] listViewType = new String[]{"Mở rộng", "Thu gọn"};
        ArrayAdapter<String> spinnerViewAdapter = new ArrayAdapter<String>(getActivity(),
                                                        R.layout.spinner_white_text, listViewType);
        spinnerViewAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        historyFavSpinnerType.setAdapter(spinnerViewAdapter);
        historyFavSpinnerType.setSelection(prefsManager.getViewType());

        String[] listSortType = new String[]{"A  -  Z", "Thời gian"};
        ArrayAdapter<String> spinnerSortAdapter = new ArrayAdapter<String>(getActivity(),
                                                        R.layout.spinner_white_text, listSortType);
        spinnerSortAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        historyFavSpinnerSort.setAdapter(spinnerSortAdapter);
        historyFavSpinnerSort.setSelection(prefsManager.getSortBy());
    }

    private void HandleSpinnerEvent(){
        historyFavSpinnerSort.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(historyFavSpinnerType.getSelectedItemPosition() == SharedPrefsManager.EXPAND){
                    historyFragment2.sortItemListView(position);
                }else{
                    historyFragment.sortItemListView(position);
                }
                prefsManager.setSortBy(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        historyFavSpinnerType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position == SharedPrefsManager.EXPAND){
                    pagerAdapter.updateFragment(historyFragment2, 0);
                    prefsManager.setViewType(position);
                    pagerAdapter.notifyDataSetChanged();
                }else{
                    pagerAdapter.updateFragment(historyFragment, 0);
                    prefsManager.setViewType(position);
                    pagerAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void SetUpViewPager(ViewPager viewPager) {
        if(historyFavSpinnerType.getSelectedItemPosition() == SharedPrefsManager.COLLAPSE){
            pagerAdapter.addFragment(historyFragment, "Lịch sử");
        }else{
            pagerAdapter.addFragment(historyFragment2, "Lịch sử");
        }
        pagerAdapter.addFragment(favoriteFragment, "Yêu thích");
        pagerAdapter.addFragment(rememberedFragment, "Đã nhớ");
        viewPager.setAdapter(pagerAdapter);
    }
}
