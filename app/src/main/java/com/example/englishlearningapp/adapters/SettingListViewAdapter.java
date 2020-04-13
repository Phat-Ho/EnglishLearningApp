package com.example.englishlearningapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.englishlearningapp.R;
import com.example.englishlearningapp.models.AlarmType;

import java.util.ArrayList;

public class SettingListViewAdapter extends BaseAdapter {
    Context context;
    ArrayList<AlarmType> alarmTypeList;

    public SettingListViewAdapter(Context context, ArrayList<AlarmType> alarmTypeList) {
        this.context = context;
        this.alarmTypeList = alarmTypeList;
    }

    @Override
    public int getCount() {
        return alarmTypeList.size();
    }

    @Override
    public Object getItem(int position) {
        return alarmTypeList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        SettingViewHolder viewHolder = null;
        if(convertView == null){
            viewHolder = new SettingViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.row_list_view_setting, null);
            viewHolder.txtNameAlarm = convertView.findViewById(R.id.txt_lv_setting);
            viewHolder.imgCheck = convertView.findViewById(R.id.img_lv_setting);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (SettingViewHolder) convertView.getTag();
        }

        //Set data from array list to view holder
        AlarmType alarmType = (AlarmType) getItem(position);
        viewHolder.txtNameAlarm.setText(alarmType.getAlarmName());
        if(alarmType.isChecked()){
            viewHolder.imgCheck.setVisibility(View.VISIBLE);
        }else{
            viewHolder.imgCheck.setVisibility(View.INVISIBLE);
        }

        return convertView;
    }

    public class SettingViewHolder{
        TextView txtNameAlarm;
        ImageView imgCheck;
    }
}
