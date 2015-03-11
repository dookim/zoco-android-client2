package com.zoco.activity;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by dookim on 2/24/15.
 */
public class ChattingService extends Service {

    @Override
    public void onCreate() {
        super.onCreate();
        //make thread for chatting

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
        //make thread for chatting
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
