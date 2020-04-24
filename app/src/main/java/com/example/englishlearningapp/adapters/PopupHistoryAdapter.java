package com.example.englishlearningapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.englishlearningapp.R;
import com.example.englishlearningapp.models.MyDate;

import java.util.ArrayList;
import java.util.Date;

public class PopupHistoryAdapter extends BaseAdapter {
    Context context;
    ArrayList<MyDate> dateList;

    public PopupHistoryAdapter(Context context, ArrayList<MyDate> dateList) {
        this.context = context;
        this.dateList = dateList;
    }

    @Override
    public int getCount() {
        return dateList.size();
    }

    @Override
    public Object getItem(int position) {
        return dateList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if(convertView == null){
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.row_list_view_popup, null);
            viewHolder.txtDate = convertView.findViewById(R.id.row_popup_date);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }

        //Set data
        MyDate myDate = (MyDate) getItem(position);
        viewHolder.txtDate.setText(myDate.getDateTime());
        return convertView;
    }

    public class ViewHolder{
        TextView txtDate;
    }
}
