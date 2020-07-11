package com.example.englishlearningapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.englishlearningapp.R;
import com.example.englishlearningapp.models.Game;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class GameHistoryAdapter extends BaseAdapter {
    Context context;
    ArrayList<Game> games;

    public GameHistoryAdapter(Context context, ArrayList<Game> games) {
        this.context = context;
        this.games = games;
    }

    @Override
    public int getCount() {
        return games != null ? games.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return games.get(position);
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
            convertView = LayoutInflater.from(context).inflate(R.layout.row_game_history, null);
            viewHolder.txtGameDate = convertView.findViewById(R.id.txt_game_date);
            viewHolder.txtRoomName = convertView.findViewById(R.id.txt_room_name);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }

        //Set data from array list to view holder
        Game game = (Game) getItem(position);
        viewHolder.txtGameDate.setText(getDatetime(game.getGameDate()));
        viewHolder.txtRoomName.setText(game.getRoomName());
        return convertView;
    }

    private class ViewHolder{
        public TextView txtGameDate, txtRoomName;
    }

    public String getDatetime(long dateTime){
        java.text.SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault());
        Date date = new Date(dateTime);
        return dateFormat.format(date);
    }
}
