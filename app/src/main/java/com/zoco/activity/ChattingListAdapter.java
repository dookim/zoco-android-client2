package com.zoco.activity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.zoco.obj.Chatting;

import java.util.ArrayList;

/**
 * Created by dookim on 2/24/15.
 */
public class ChattingListAdapter extends BaseAdapter{

    Context context;
    ArrayList<Chatting> chattings;

    public ChattingListAdapter(Context context, ArrayList<Chatting> chattings) {
        this.context = context;
        this.chattings = chattings;
    }
    @Override
    public int getCount() {
        return chattings.size();
    }

    @Override
    public Chatting getItem(int position) {
        return chattings.get(position);
    }

    @Override
    public long getItemId(int position) {
        return chattings.hashCode();
    }

    //view를 두개 리턴할것이라고 명시해줌
    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Chatting chatting = getItem(position);

        if (convertView == null) {
            // Inflate the layout according to the view type
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if (chatting.who.equals(Chatting.Who.me)) {
                // Inflate the layout with image
                convertView = inflater.inflate(R.layout.my_chatting, parent, false);
            } else {
                convertView = inflater.inflate(R.layout.opposite_chatting, parent, false);
            }
        }

        TextView chatTV = (TextView)convertView.findViewById(R.id.chatting_text);
        chatTV.setText(chatting.text);

        return convertView;
    }
}
