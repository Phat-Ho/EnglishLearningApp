package com.example.englishlearningapp.adapters;

import android.os.Parcelable;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;

import java.util.ArrayList;

public class HistoryViewPagerAdapter extends FragmentStatePagerAdapter {
    private static final String TAG = "HistoryViewPagerAdapter";
    private ArrayList<Fragment> fragmentList = new ArrayList<>();
    private ArrayList<String> fragmentTitle = new ArrayList<>();

    @Nullable
    @Override
    public Parcelable saveState() {
        return null;
    }

    public HistoryViewPagerAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        Log.d(TAG, "getItem: " + position);
        return fragmentList.get(position);
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return fragmentTitle.get(position);
    }

    public void addFragment(Fragment pFragment, String pTitle){
        fragmentList.add(pFragment);
        fragmentTitle.add(pTitle);
    }

    public void updateFragment(Fragment pFrag, int position){
        fragmentList.set(position, pFrag);
    }
}
