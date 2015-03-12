package com.zoco.activity;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;
import com.zoco.common.ZocoPassword;
import com.zoco.obj.User;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by user on 2015-03-09.
 */
public class FaceBookAccountManager {

    private static final String TAG = "NARA";

    private UiLifecycleHelper uiHelper;
    private GraphUser user;
    private Context context;

    public FaceBookAccountManager(Context context) {
        this.context = context;
    }

    public void onSessionStateChange(Session session, SessionState state) {
        if (state.isOpened()) {
            // Request user data and show the results
            Request.newMeRequest(session, new Request.GraphUserCallback() {

                public void onCompleted(GraphUser user, Response response) {
                    if (response != null) {
                        if (user != null) {
                            String email;
                            String univ;
                            String provider = "facebook";
                            String password;
                            String nickname;
                            try {
                                Log.d(TAG, "onSessionStateChange ::: ");
                                response.getError();
                                email = user.getProperty("email").toString();
                                nickname = email.split("\\@")[0];
                                JSONArray education = (JSONArray) user
                                        .getProperty("education");
                                if (education.length() > 0) {
                                    for (int i = 0; i < education.length(); i++) {
                                        JSONObject edu_obj = education.optJSONObject(i);

                                        String type = edu_obj.optString("type");

                                        if (type.equalsIgnoreCase("college")) {
                                            JSONObject school_obj = edu_obj
                                                    .optJSONObject("school");
                                            univ = school_obj
                                                    .optString("name");

                                            password = ZocoPassword.createPassword(provider + email + univ);

                                            User info = new User(email, nickname, provider, univ, password);
                                            Log.d(TAG, "email : " + email);
                                            Log.d(TAG, "univ : " + univ);
                                            Log.d(TAG, "provider : " + provider);
                                            Log.d(TAG, "password : " + password);
                                            Log.d(TAG, "nickname : " + nickname);

                                        }
                                    }
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                                Log.d(TAG, "error!!!!!");
                            }
                        }
                    }
                }

            }).executeAsync();
        } else if (state.isClosed()) {
            Log.i(TAG, " session is closed ");
        }
    }

    public Session.StatusCallback callback = new Session.StatusCallback() {
        public void call(Session session, SessionState state,
                         Exception exception) {
            if (session != null && session.isOpened()) {
                context.startActivity(new Intent(context, MainActivity.class));

            }
//            } else {
//                onSessionStateChange(session, state);
//            }
        }
    };
}
