package com.example.englishlearningapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.example.englishlearningapp.R;

import java.util.List;

public class ConnectedWordAdapter extends BaseAdapter {

    private Context context;
    private int layout;
    private List<Integer> arrImage;

    public ConnectedWordAdapter(Context context, int layout, List<Integer> arrImage) {
        this.context = context;
        this.layout = layout;
        this.arrImage = arrImage;
    }

    @Override
    public int getCount() {
        return arrImage.size();
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
        ImageView imgConnectedWord;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(layout, null);

            viewHolder = new ViewHolder();
            viewHolder.imgConnectedWord = convertView.findViewById(R.id.imageViewConnectedWord);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Integer img = arrImage.get(position);
        viewHolder.imgConnectedWord.setImageResource(img);
        return convertView;
    }
}
