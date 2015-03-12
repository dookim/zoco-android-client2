package com.zoco.common;

import android.os.Handler;
import android.os.Message;


/**
 * Created by dookim on 2/14/15.
 */
public abstract class ZocoHandler extends Handler {

    public void handleMessage(Message msg) {
        String result = (String)msg.obj;
        onReceive(result);
    }

    public abstract void onReceive(String result);
}
