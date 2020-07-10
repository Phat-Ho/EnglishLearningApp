package com.example.englishlearningapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.englishlearningapp.R;
import com.example.englishlearningapp.models.HistoryGameWord;

import java.util.ArrayList;

public class HistoryGameAdapter extends BaseAdapter {
    Context context;
    ArrayList<HistoryGameWord> arrayList;

    public HistoryGameAdapter(Context context, ArrayList<HistoryGameWord> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @Override
    public int getCount() {
        return arrayList != null ? arrayList.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return arrayList.get(position);
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
            convertView = LayoutInflater.from(context).inflate(R.layout.row_history_game, null);
            viewHolder.txtPlayer = convertView.findViewById(R.id.txt_player_history_game);
            viewHolder.txtWord = convertView.findViewById(R.id.txt_word_history_game);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }

        //Set data from array list to view holder
        HistoryGameWord gameWord = (HistoryGameWord) getItem(position);
        viewHolder.txtWord.setText(gameWord.getWord());
        viewHolder.txtPlayer.setText(gameWord.getPlayer());
        return convertView;
    }

    private class ViewHolder{
        TextView txtWord;
        TextView txtPlayer;
    }
}
