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
        TextView txtRoomName, txtRoomOwner;
        ImageView imgPassword;
        ImageButton imgBtnRoomInfo;
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
            viewHolder.imgPassword = convertView.findViewById(R.id.imageViewPassword);
            viewHolder.imgBtnRoomInfo = convertView.findViewById(R.id.imageButtonRoomInfo);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Room room = arrRoom.get(position);

        viewHolder.txtRoomName.setText(room.getName());
        viewHolder.txtRoomOwner.setText(room.getName());
        viewHolder.imgPassword.setFocusable(false);
        viewHolder.imgPassword.setFocusableInTouchMode(false);
        viewHolder.imgBtnRoomInfo.setFocusable(false);
        viewHolder.imgBtnRoomInfo.setFocusableInTouchMode(false);
        if (room.getPassword().equals("")){
            viewHolder.imgPassword.setVisibility(View.INVISIBLE);
        } else {
            viewHolder.imgPassword.setVisibility(View.VISIBLE);
        }

        return convertView;
    }
}