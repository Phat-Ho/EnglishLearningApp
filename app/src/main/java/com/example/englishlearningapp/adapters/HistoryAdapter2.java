package com.example.englishlearningapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.englishlearningapp.R;
import com.example.englishlearningapp.models.Word;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class HistoryAdapter2 extends BaseAdapter {
    private Context context;
    private int layout;
    private ArrayList<Word> historyWords;

    public HistoryAdapter2(Context context, int layout, ArrayList<Word> historyWords) {
        this.context = context;
        this.layout = layout;
        this.historyWords = historyWords;
    }

    @Override
    public int getCount() {
        return historyWords.size();
    }

    @Override
    public Object getItem(int position) {
        return historyWords.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private class ViewHolder{
        TextView txtHistoryWord, txtDate;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(layout, null);
            viewHolder = new ViewHolder();
            viewHolder.txtHistoryWord = convertView.findViewById(R.id.txt_word_row_history_2);
            viewHolder.txtDate = convertView.findViewById(R.id.txt_date_row_history_2);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        Word word = historyWords.get(position);
        viewHolder.txtHistoryWord.setText(word.getWord());
        viewHolder.txtDate.setText(getDatetime(word.getDate()));
        return convertView;
    }

    public String getDatetime(long dateTime){
        java.text.SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault());
        Date date = new Date(dateTime);
        return dateFormat.format(date);
    }
}
