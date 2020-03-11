package com.example.englishlearningapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.englishlearningapp.R;

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
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        holder.name.setText((String) subjectNames.get(position));
        holder.image.setImageResource((Integer) subjectImages.get(position));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "You clicked " + subjectNames.get(position), Toast.LENGTH_SHORT).show();
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
