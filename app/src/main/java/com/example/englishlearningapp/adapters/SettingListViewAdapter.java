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
import com.example.englishlearningapp.fragments.SettingFragment;
import com.example.englishlearningapp.models.AlarmType;

import java.util.ArrayList;

public class SettingListViewAdapter extends BaseAdapter {
    SettingFragment context;
    ArrayList<AlarmType> alarmTypeList;

    public SettingListViewAdapter(SettingFragment context, ArrayList<AlarmType> alarmTypeList) {
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
            convertView = LayoutInflater.from(context.getActivity()).inflate(R.layout.row_list_view_setting, null);
            viewHolder.txtNameAlarm = convertView.findViewById(R.id.txt_lv_setting);
            viewHolder.imgCheck = convertView.findViewById(R.id.img_lv_setting);
            viewHolder.imgBtnInfoSetting = convertView.findViewById(R.id.row_lv_setting_img_btn_info);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (SettingViewHolder) convertView.getTag();
        }

        //Set data from array list to view holder
        final AlarmType alarmType = (AlarmType) getItem(position);
        viewHolder.txtNameAlarm.setText(alarmType.getAlarmName());
        viewHolder.imgBtnInfoSetting.setFocusable(false);
        viewHolder.imgBtnInfoSetting.setFocusableInTouchMode(false);
        viewHolder.imgBtnInfoSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.showPopupWordList(alarmType.getAlarmId());
            }
        });
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
        ImageButton imgBtnInfoSetting;
    }
}
