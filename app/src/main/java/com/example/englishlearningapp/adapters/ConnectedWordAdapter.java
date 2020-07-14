package com.example.englishlearningapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.englishlearningapp.R;
import com.example.englishlearningapp.models.ConnectedWord;

import org.w3c.dom.Text;

import java.util.List;

public class ConnectedWordAdapter extends BaseAdapter {

    private Context context;
    private int layout;
    private List<ConnectedWord> arrConnectedWords;

    public ConnectedWordAdapter(Context context, int layout, List<ConnectedWord> arrConnectedWords) {
        this.context = context;
        this.layout = layout;
        this.arrConnectedWords = arrConnectedWords;
    }

    @Override
    public int getCount() {
        return arrConnectedWords.size();
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
        TextView txtConnectedWord;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(layout, null);

            viewHolder = new ViewHolder();
            viewHolder.imgConnectedWord = convertView.findViewById(R.id.imageViewConnectedWord);
            viewHolder.txtConnectedWord = convertView.findViewById(R.id.textViewConnectedWord);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        ConnectedWord connectedWord = arrConnectedWords.get(position);
        viewHolder.imgConnectedWord.setImageResource(connectedWord.getImage());
        viewHolder.txtConnectedWord.setText(connectedWord.getName());
        return convertView;
    }
}
