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

public class RememberedAdapter extends BaseAdapter {
    private Context context;
    private int layout;
    private ArrayList<Word> rememberedWords;

    public RememberedAdapter(Context context, int layout, ArrayList<Word> rememberedWords) {
        this.context = context;
        this.layout = layout;
        this.rememberedWords = rememberedWords;
    }

    @Override
    public int getCount() {
        return rememberedWords.size();
    }

    @Override
    public Object getItem(int position) {
        return rememberedWords.get(position);
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
            viewHolder.txtWord = convertView.findViewById(R.id.txt_word_row_remembered);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        Word word = rememberedWords.get(position);
        viewHolder.txtWord.setText(word.getWord());
        return convertView;
    }
}
