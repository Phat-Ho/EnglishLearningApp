package com.example.englishlearningapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.englishlearningapp.R;
import com.example.englishlearningapp.models.Room;

import java.util.List;

public class RoomAdapter extends BaseAdapter {

    private Context context;
    private int layout;
    private List<Room> arrRoom;

    public RoomAdapter(Context context, int layout, List<Room> arrRoom) {
        this.context = context;
        this.layout = layout;
        this.arrRoom = arrRoom;
    }

    @Override
    public int getCount() {
        return arrRoom.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    private class ViewHolder{
        TextView txtRoomName, txtRoomOwner, txtRoomPlayerCount, txtStatus;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(layout, null);
            viewHolder = new ViewHolder();
            viewHolder.txtRoomName = convertView.findViewById(R.id.textViewRoomName);
            viewHolder.txtRoomOwner = convertView.findViewById(R.id.textViewRoomOwner);
            viewHolder.txtRoomPlayerCount = convertView.findViewById(R.id.textViewRoomPlayer);
            viewHolder.txtStatus = convertView.findViewById(R.id.txt_room_status);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Room room = arrRoom.get(position);

        viewHolder.txtRoomName.setText(room.getName());
        viewHolder.txtRoomOwner.setText(room.getCreator());
        viewHolder.txtRoomPlayerCount.setText(room.getPlayerCount() + "/" + room.getNumOfPlayers());
        viewHolder.txtStatus.setText(room.isPlaying() ? "Đang chơi" : "Đang chờ");
        return convertView;
    }
}
