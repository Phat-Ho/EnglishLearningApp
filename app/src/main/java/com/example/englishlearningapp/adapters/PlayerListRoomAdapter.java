package com.example.englishlearningapp.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.englishlearningapp.R;
import com.example.englishlearningapp.models.Player;
import com.example.englishlearningapp.utils.LoginManager;

import java.util.ArrayList;

public class PlayerListRoomAdapter extends BaseAdapter {
    private static final String TAG = "PlayerListRoomAdapter";
    private Context context;
    private ArrayList<Player> players;
    private LoginManager loginManager;

    public PlayerListRoomAdapter(Context context, ArrayList<Player> players) {
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
    public boolean isEnabled(int position) {
        return false;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if(convertView == null){
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.row_list_view_player, null);
            viewHolder.txtPlayerName = convertView.findViewById(R.id.txt_player_name_lv_room);
            viewHolder.imgBtnKick = convertView.findViewById(R.id.img_btn_kick_lv_room);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }

        //Set data from array list to view holder
        int loggedId = loginManager.getUserId();
        if(players.get(0).getId() != loggedId){
            viewHolder.imgBtnKick.setVisibility(View.INVISIBLE);
        }else{
            viewHolder.imgBtnKick.setVisibility(View.VISIBLE);
        }
        Player player = (Player) getItem(position);
        if(player.isPlay()){
            viewHolder.txtPlayerName.setText(player.getName());
            if(loggedId == player.getId()){
                Log.d(TAG, "set color: " + player.toString());
                viewHolder.txtPlayerName.setTextColor(context.getResources().getColor(R.color.colorGreen));
                viewHolder.imgBtnKick.setVisibility(View.INVISIBLE);
            }else{
                viewHolder.txtPlayerName.setTextColor(context.getResources().getColor(R.color.black));
            }
        }
        return convertView;
    }

    private class ViewHolder{
        private TextView txtPlayerName;
        private ImageButton imgBtnKick;
    }
}
