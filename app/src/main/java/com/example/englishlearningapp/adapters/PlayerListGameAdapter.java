package com.example.englishlearningapp.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.englishlearningapp.R;
import com.example.englishlearningapp.models.Player;
import com.example.englishlearningapp.utils.LoginManager;

import java.util.ArrayList;

public class PlayerListGameAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<Player> players;
    private LoginManager loginManager;

    public PlayerListGameAdapter(Context context, ArrayList<Player> players) {
        this.context = context;
        this.players = players;
        loginManager = new LoginManager(context);
    }

    @Override
    public int getCount() {
        return players != null ? players.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return players.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        PlayerListViewHolder viewHolder;
        if(convertView == null){
            viewHolder = new PlayerListViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.row_list_view_players_order, null);
            viewHolder.txtPlayerName = convertView.findViewById(R.id.txt_name_lv_players_game);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (PlayerListViewHolder) convertView.getTag();
        }

        //Set data from array list to view holder
        Player player = (Player) getItem(position);
        viewHolder.txtPlayerName.setText(player.getName());
        if(loginManager.getUserId() == player.getId()){
            viewHolder.txtPlayerName.setTextColor(context.getResources().getColor(R.color.colorGreen));
        }else{
            viewHolder.txtPlayerName.setTextColor(context.getResources().getColor(R.color.black));
        }

        return convertView;
    }

    public class PlayerListViewHolder{
        public TextView txtPlayerName;
    }
}
