package com.example.englishlearningapp.adapters;

import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.englishlearningapp.R;
import com.example.englishlearningapp.models.Subject;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class SubjectAdapter extends RecyclerView.Adapter<SubjectAdapter.SubjectCardViewHolder> {
    ArrayList<Subject> subjectArrayList;

    public SubjectAdapter(ArrayList<Subject> subjectArrayList) {
        this.subjectArrayList = subjectArrayList;
    }

    @NonNull
    @Override
    public SubjectCardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //inflate item card layout subject_card.xml
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.subject_card, null);
        SubjectCardViewHolder subjectCardViewHolder = new SubjectCardViewHolder(view);
        return subjectCardViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull SubjectCardViewHolder holder, int position) {
        //binding subject data to view holder
        if(subjectArrayList != null && position < subjectArrayList.size()){
            Subject subject =subjectArrayList.get(position);
            holder.subjectName.setText(subject.getSubjectName());
            //Use picasso library to add image url to image view
            Picasso.get().load(subject.getSubjectImage()).placeholder(R.mipmap.ic_launcher).error(R.drawable.home_ic_1).into(holder.subjectImage);
        }
    }

    @Override
    public int getItemCount() {
        return subjectArrayList.size();
    }

    public class SubjectCardViewHolder extends RecyclerView.ViewHolder {
        public ImageView subjectImage;
        public TextView subjectName;

        public SubjectCardViewHolder(@NonNull View itemView) {
            super(itemView);

            //Mapping view from subject_card.xml
            subjectImage = itemView.findViewById(R.id.subject_card_img);
            subjectName = itemView.findViewById(R.id.subject_card_txt);
        }
    }
}
