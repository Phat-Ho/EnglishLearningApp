package com.example.englishlearningapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.englishlearningapp.R;
import com.example.englishlearningapp.models.Word;

import java.util.ArrayList;

public class FavoriteAdapter extends BaseAdapter {
    private Context context;
    private int layout;
    private ArrayList<Word> historyWords;

    public FavoriteAdapter(Context context, int layout, ArrayList<Word> historyWords) {
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
        TextView txtWord;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null){
            viewHolder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(layout, null);
            viewHolder.txtWord = convertView.findViewById(R.id.txt_word_row_favorite);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        Word word = historyWords.get(position);
        viewHolder.txtWord.setText(word.getWord());
        return convertView;
    }
}
