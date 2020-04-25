package com.example.englishlearningapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.englishlearningapp.R;
import com.example.englishlearningapp.fragments.HistoryFragment;
import com.example.englishlearningapp.models.Word;

import java.util.ArrayList;

public class HistoryAdapter extends BaseAdapter {

    private HistoryFragment context;
    private int layout;
    private ArrayList<Word> historyWords;

    public HistoryAdapter(HistoryFragment context, int layout, ArrayList<Word> historyWords) {
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
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    class ViewHolder{
        TextView txtHistoryWord;
        ImageButton imgBtnInfo;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null){
            LayoutInflater inflater = (LayoutInflater) context.getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(layout, null);
            viewHolder = new ViewHolder();
            viewHolder.txtHistoryWord = convertView.findViewById(R.id.textViewHistoryWord);
            viewHolder.imgBtnInfo = convertView.findViewById(R.id.imageButtonInfo);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        final Word historyWord = historyWords.get(position);
        viewHolder.txtHistoryWord.setText(historyWord.getWord());
        viewHolder.imgBtnInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.showPopup(historyWord.getId(), historyWord.getWord(), historyWord.getDescription());
            }
        });
        return convertView;
    }
}
