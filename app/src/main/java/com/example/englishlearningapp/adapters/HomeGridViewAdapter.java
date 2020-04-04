package com.example.englishlearningapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.englishlearningapp.activity.MainHomeActivity;
import com.example.englishlearningapp.R;
import com.example.englishlearningapp.activity.HistoryActivity;
import com.example.englishlearningapp.activity.ScheduleActivity;
import com.example.englishlearningapp.fragments.SettingFragment;
import com.example.englishlearningapp.fragments.SubjectsFragment;
import com.example.englishlearningapp.navigation_bottom_fragments.HomeFragment;

import java.util.ArrayList;

public class HomeGridViewAdapter extends RecyclerView.Adapter <HomeGridViewAdapter.MyViewHolder>{

    ArrayList subjectNames;
    ArrayList subjectImages;
    Context context;
    SubjectsFragment subjectsFragment = new SubjectsFragment();
    HomeFragment homeFragment;
    SettingFragment settingFragment = new SettingFragment();

    public HomeGridViewAdapter(Context context, ArrayList subjectNames, ArrayList subjectImages, HomeFragment homeFragment) {
        this.context = context;
        this.subjectNames = subjectNames;
        this.subjectImages = subjectImages;
        this.homeFragment = homeFragment;
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // infalte the item Layout
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_layout, parent, false);
        // set the view's size, margins, paddings and layout parameters
        MyViewHolder vh = new MyViewHolder(v); // pass the view to View Holder
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        holder.name.setText((String) subjectNames.get(position));
        holder.image.setImageResource((Integer) subjectImages.get(position));
        final MainHomeActivity mainHomeActivity = (MainHomeActivity) context;
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(context, "You clicked " + subjectNames.get(position), Toast.LENGTH_SHORT).show();
                switch (position){
                    case 0:
                        /*Intent subjectIntent = new Intent(context, SubjectActivity.class);
                        context.startActivity(subjectIntent);*/
                        mainHomeActivity.showFragment(subjectsFragment);
                        break;
                    case 1:
                        mainHomeActivity.showFragment(settingFragment);
                        Toast.makeText(context, "Đọc", Toast.LENGTH_SHORT).show(); break;
                    case 2:
                        Intent scheduleIntent = new Intent(context, ScheduleActivity.class);
                        context.startActivity(scheduleIntent);
                        break;
                    case 3:
                        Toast.makeText(context, "Viết", Toast.LENGTH_SHORT).show(); break;
                    case 4:
                        //Navigate to History Activity
                        Intent historyIntent = new Intent(context, HistoryActivity.class);
                        context.startActivity(historyIntent);
                        break;
                    case 5:
                        Toast.makeText(context, "Thi đấu", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });
    }


    @Override
    public int getItemCount() {
        return subjectNames.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        // init the item view's
        TextView name;
        ImageView image;
        public MyViewHolder(View itemView) {
            super(itemView);
            // get the reference of item view's
            name = (TextView) itemView.findViewById(R.id.textViewHomeItem);
            image = (ImageView) itemView.findViewById(R.id.imageViewHomeItem);
        }
    }

}
