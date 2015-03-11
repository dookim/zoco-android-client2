package com.zoco.activity;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;

import com.zoco.obj.Chatting;

import java.util.ArrayList;

/**
 * Created by dookim on 2/24/15.
 */
public class ChattingActivity extends Activity {

    ListView lv;
    ChattingListAdapter adapter;
    ArrayList<Chatting> chattings;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chatting_main);

        lv = (ListView)findViewById(R.id.chattingLV);


        //read ex data from db!! in here!

        chattings = new ArrayList<Chatting>();
        adapter = new ChattingListAdapter(getBaseContext(), chattings);
        lv.setAdapter(adapter);


    }
}
