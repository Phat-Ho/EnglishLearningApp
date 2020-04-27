package com.example.englishlearningapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.englishlearningapp.R;
import com.example.englishlearningapp.models.Word;

import java.util.ArrayList;

public class SettingListViewPopupAdapter extends BaseAdapter {
    ArrayList<Word> wordList;
    Context context;

    public SettingListViewPopupAdapter(ArrayList<Word> wordList, Context context) {
        this.wordList = wordList;
        this.context = context;
    }

    @Override
    public int getCount() {
        return wordList.size();
    }

    @Override
    public Object getItem(int position) {
        return wordList.get(position);
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
            convertView = LayoutInflater.from(context).inflate(R.layout.row_lv_setting_popup, null);
            viewHolder.txtRowSettingPopup = convertView.findViewById(R.id.txt_row_setting_popup);
            viewHolder.imgRowSettingPopup = convertView.findViewById(R.id.img_row_setting_popup);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }

        //Set data
        Word word = (Word) getItem(position);
        viewHolder.txtRowSettingPopup.setText(word.getWord());
        if(word.getRemembered() == 1){
            viewHolder.imgRowSettingPopup.setVisibility(View.VISIBLE);
        }
        return convertView;
    }

    class ViewHolder{
        TextView txtRowSettingPopup;
        ImageView imgRowSettingPopup;
    }
}
