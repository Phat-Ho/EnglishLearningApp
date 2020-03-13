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
import androidx.recyclerview.widget.RecyclerView;

import com.example.englishlearningapp.MainHomeActivity;
import com.example.englishlearningapp.R;
import com.example.englishlearningapp.activity.SubjectActivity;

import java.util.ArrayList;

public class HomeGridViewAdapter extends RecyclerView.Adapter <HomeGridViewAdapter.MyViewHolder>{

    ArrayList subjectNames;
    ArrayList subjectImages;
    Context context;

    public HomeGridViewAdapter(Context context, ArrayList subjectNames, ArrayList subjectImages) {
        this.context = context;
        this.subjectNames = subjectNames;
        this.subjectImages = subjectImages;
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
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(context, "You clicked " + subjectNames.get(position), Toast.LENGTH_SHORT).show();
                switch (position){
                    case 0:
                        Intent subjectIntent = new Intent(context, SubjectActivity.class);
                        context.startActivity(subjectIntent);
                        break;
                    case 1:
                        Toast.makeText(context, "Đọc", Toast.LENGTH_SHORT).show(); break;
                    case 2:
                        Toast.makeText(context, "Nghe", Toast.LENGTH_SHORT).show(); break;
                    case 3:
                        Toast.makeText(context, "Viết", Toast.LENGTH_SHORT).show(); break;
                    case 4:
                        Toast.makeText(context, "Test", Toast.LENGTH_SHORT).show(); break;
                    case 5:
                        Toast.makeText(context, "Thi đấu", Toast.LENGTH_SHORT).show();
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
