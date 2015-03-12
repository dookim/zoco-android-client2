package com.zoco.common;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.gson.Gson;
import com.zoco.obj.User;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by user on 2015-02-17.
 */
public class ZocoPreference {

    private SharedPreferences pref;
    private final String PREF_NAME = "com.zoco.preference";
    private Context mContext;

    public ZocoPreference(Context context) {
        mContext = context;
        pref = mContext.getSharedPreferences(PREF_NAME,
                mContext.MODE_PRIVATE);
    }

    public void put(String provider, User user) {
        SharedPreferences.Editor editor = pref.edit();

        Gson gson = new Gson();
        String jsonInformation = gson.toJson(user);
        Log.d("NARA", " preference put : " + jsonInformation);
        editor.putString(provider, jsonInformation);

        editor.commit();
    }

    public void putLogin(boolean isLogin) {
        SharedPreferences.Editor editor = pref.edit();

        editor.putBoolean("login", isLogin);
        editor.commit();
    }


    public User get(String provider) {
        User userInformation = null;
        Log.d("NARA", " preference provider : " + provider);
        String jsonInformation = pref.getString(provider, "");
        Log.d("NARA", " preference jsonInformation : " + jsonInformation);
        Gson gson = new Gson();
        userInformation = gson.fromJson(jsonInformation, User.class);

        Log.d("NARA", " preference get : " + userInformation);

        return userInformation;
    }

    public boolean getLoginValue() {
        return pref.getBoolean("login", false);
    }
}
