package com.example.englishlearningapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.englishlearningapp.R;
import com.example.englishlearningapp.models.Rank;

import java.util.ArrayList;

public class RankAdapter extends BaseAdapter {

    Context context;
    ArrayList<Rank> ranks;

    public RankAdapter(Context context, ArrayList<Rank> ranks) {
        this.context = context;
        this.ranks = ranks;
    }

    @Override
    public int getCount() {
        return ranks != null ? ranks.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return ranks.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if(convertView == null){
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.row_rank, null);
            viewHolder.txtGameDate = convertView.findViewById(R.id.txt_player_name);
            viewHolder.txtRoomName = convertView.findViewById(R.id.txt_point);
            viewHolder.imgRowRank = convertView.findViewById(R.id.img_row_rank);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }

        //Set data from array list to view holder
        Rank rank = (Rank) getItem(position);
        switch (position){
            case 0: viewHolder.imgRowRank.setImageResource(R.drawable.number_1_icon); break;
            case 1: viewHolder.imgRowRank.setImageResource(R.drawable.number_2_icon); break;
            case 2: viewHolder.imgRowRank.setImageResource(R.drawable.number_3_icon); break;
        }
        viewHolder.txtGameDate.setText(rank.getPlayerName());
        viewHolder.txtRoomName.setText(String.valueOf(rank.getPoint()));
        return convertView;
    }

    private class ViewHolder{
        public TextView txtGameDate, txtRoomName;
        public ImageView imgRowRank;
    }
}
