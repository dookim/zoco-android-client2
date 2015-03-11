package com.zoco.common;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;


/**
 * Created by duhyeong1.kim on 2015-02-10.
 */
public class ReqTask extends AsyncTask<String, Void, String> {

    Context context;
    ZocoNetwork.Method method;
    Handler handler;

    public ReqTask(Context context, ZocoNetwork.Method method) {
        this.context = context;
        this.method = method;
    }

    public ReqTask setHandler(Handler handler) {
        this.handler = handler;
        return this;
    }

    @Override
    protected String doInBackground(String... params) {
        // TODO Auto-generated method stub
        //두개의 모드로 분리해야함
        String result = "";
        try {
            if (method.equals(ZocoNetwork.Method.GET)) {
                String url = params[0];
                result = new ZocoNetwork().setGetOption(url).execute();
            } else if (method.equals(ZocoNetwork.Method.POST)) {
                String url = params[0];
                String data = params[1];
                result = new ZocoNetwork().setPostOption(url,data).execute();
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            result = e.getMessage();
        }
        return result;

    }

    @Override
    protected void onPostExecute(String result) {
        // TODO Auto-generated method stub
        Toast.makeText(context, result, Toast.LENGTH_LONG).show();
        if(handler != null) {
            Message msg = new Message();
            msg.obj = result;
            handler.sendMessage(msg);
        }
        super.onPostExecute(result);
    }

}