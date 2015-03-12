package com.zoco.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.Session;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.SignInButton;
import com.google.gson.Gson;
import com.zoco.common.ReadExcel;
import com.zoco.common.ReqTask;
import com.zoco.common.ZocoHandler;
import com.zoco.common.ZocoNetwork;
import com.zoco.common.ZocoPreference;
import com.zoco.obj.User;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class LoginActivity extends Activity {

    private static final String TAG = "NARA";

    private GoogleAccountManager mGoogleAccntMgr;
    private FaceBookAccountManager mFacebookAccntMgr;
    private SignInButton mGoogleBtn;
    private LoginButton facebookBtn;
    private List<String> mFacebookPermits;

    private boolean isRegister = true;

    private UiLifecycleHelper uiHelper;
    private GraphUser user;

    private ZocoPreference mPref;
    private List<User> mRegisterUserList;
    private ArrayList<String> mRegisterAccountProvider;
    private Handler mHandler;


    private String email;
    private String univ;
    private String provider;
    private String password;
    private String nickname;

    private EditText nick;
    private AutoCompleteTextView school;

    private Context mContext;

    private enum Provider {
        facebook, google;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFacebookAccntMgr = new FaceBookAccountManager(this);
        uiHelper = new UiLifecycleHelper(this, mFacebookAccntMgr.callback);
        uiHelper.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_activity_test);

        mRegisterUserList = new ArrayList<User>();
        mPref = new ZocoPreference(this);

        initGoogle();
        initFacebook();

        Intent intent = getIntent();
        if (intent != null && intent.getStringExtra("logout") != null) {
            setLogOut();
        }

        if (isLoginValueInPref()) {
            for (Provider p : Provider.values()) {
                User user = getUserOfProvider(p.name());  //pref에 프로바이더 정보가 있는 지 본다
                if (user != null) {
                    mRegisterUserList.add(user); // 해당 pref에 사용자 정보가 있으면 registerlist에 추가
                }
            }
        }

        int numOfRegisterUser = mRegisterUserList.size();   //  등록된 계정이 몇개인지
        Log.d(TAG, "numOfRegisterUser  : " + numOfRegisterUser);

        if (numOfRegisterUser == 1) {
            // 등록된 계정 하나, mRegisterUserList에 갖고 있는 user 객체 정보 가져옴
            email = mRegisterUserList.get(0).email;
            univ = mRegisterUserList.get(0).univ;
            provider = mRegisterUserList.get(0).provider;
            password = mRegisterUserList.get(0).password;
            nickname = mRegisterUserList.get(0).nickname;
            Log.d(TAG, " numOfRegisterUser == 1 ");
            checkRegister(nickname);

        } else if (numOfRegisterUser > 1) {
            // 등록된 계정 둘 이상
            Log.d(TAG, " numOfRegisterUser > 1 ");
            mRegisterAccountProvider = new ArrayList<String>();
            for (User user : mRegisterUserList) {
                mRegisterAccountProvider.add(user.provider);
                Log.d(TAG, " provider : " + user.provider);
            }

            new AlertDialog.Builder(LoginActivity.this)
                    .setTitle("Select Provider")
                    .setSingleChoiceItems(mRegisterAccountProvider.toArray(new String[mRegisterAccountProvider.size()]), 0,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    provider = mRegisterAccountProvider.get(whichButton);
                                    Log.d(TAG, "singleChoice account : " + provider);
                                }
                            })
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            dialog.dismiss();
                        }
                    })
                    .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            dialog.dismiss();
                        }
                    })
                    .create().show();

            for (User user : mRegisterUserList) {
                if (user.provider.equals(provider)) {
                    email = user.email;
                    univ = user.univ;
                    password = user.password;
                    nickname = user.nickname;
                    break;
                }
            }
            checkRegister(nickname);
        }

        isRegister = false;
    }

    private boolean isLoginValueInPref() {
        return mPref.getLoginValue();
    }

    private User getUserOfProvider(String provider) {
        User user = mPref.get(provider);
        return user;
    }

    private void initFacebook() {

        mFacebookPermits = Arrays.asList("public_profile",
                "user_education_history", "email");
        facebookBtn = (LoginButton) findViewById(R.id.login_button);
        facebookBtn.setReadPermissions(mFacebookPermits);
        facebookBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Session session = Session.getActiveSession();
                mFacebookAccntMgr.onSessionStateChange(session, session.getState());
            }
        });


    }

    private void initGoogle() {
        Log.d(TAG, "initGoogle");
        mGoogleAccntMgr = new GoogleAccountManager(this, this) {
            @Override
            protected void onPlusClientRevokeAccess() {

            }

            @Override
            protected void onPlusClientSignIn() {

            }

            @Override
            protected void onPlusClientSignOut() {

            }

            @Override
            protected void onPlusClientBlockingUI(boolean show) {

            }

            @Override
            protected void updateConnectButtonState() {
                boolean connected = getPlusClient().isConnected();
                Log.d(TAG, "conencted : " + connected);
                if (connected) {
//                    mGoogleAccntMgr.signOut();
                    User user = mGoogleAccntMgr.getUser();
                    Log.d(TAG, "user : " + user);
                    mPref.put(Provider.google.name(), user);
                    registerUser(user.email, user.nickname, user.provider, user.univ, user.password);
                }
            }

            @Override
            public void onConnectionSuspended(int i) {

            }

            @Override
            public void onAccessRevoked(ConnectionResult connectionResult) {

            }
        };

        mGoogleBtn = (SignInButton) findViewById(R.id.plus_sign_in_button);
        if (supportsGooglePlayServices()) {
            // Set a listener to connect the user when the G+ button is clicked.
            mGoogleBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mGoogleAccntMgr.signIn();
                }
            });
        } else {
            // Don't offer G+ sign in if the app's version is too low to support Google Play
            // Services.
            mGoogleBtn.setVisibility(View.GONE);
            return;
        }

    }

    private boolean supportsGooglePlayServices() {
        return GooglePlayServicesUtil.isGooglePlayServicesAvailable(this) ==
                ConnectionResult.SUCCESS;
    }

    public void startMainActivity() {
        startActivity(new Intent(LoginActivity.this, MainActivity.class));
        finish();
    }

    private void setLogOut() {
        Log.d(TAG, "logout ***");
        mGoogleAccntMgr.signOut();

        Session session = Session.getActiveSession();
        if (session != null && session.isOpened()) {    //세션이 열려있으면 pref에 로그아웃 시킨 후, 세션 클로즈
            Log.d(TAG, "logout ***");
            mPref.putLogin(false);
            Session.getActiveSession().closeAndClearTokenInformation();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Session session = Session.getActiveSession();
        if (session != null && !isRegister && (session.isOpened() || session.isClosed())) {
            mFacebookAccntMgr.onSessionStateChange(session, session.getState());
        }
        uiHelper.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();


    }

    @Override
    protected void onStop() {
        super.onStop();
        mGoogleAccntMgr.initiatePlusClientDisconnect();
        uiHelper.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        uiHelper.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int responseCode, Intent intent) {
        uiHelper.onActivityResult(requestCode, responseCode, intent);
        mGoogleAccntMgr.updateConnectButtonState();
        if (requestCode == mGoogleAccntMgr.OUR_REQUEST_CODE && responseCode == RESULT_OK) {
            mGoogleAccntMgr.mAutoResolveOnFail = true;
            mGoogleAccntMgr.initiatePlusClientConnect();
        } else if (requestCode == mGoogleAccntMgr.OUR_REQUEST_CODE && responseCode != RESULT_OK) {
            mGoogleAccntMgr.setProgressBarVisible(false);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        uiHelper.onSaveInstanceState(outState);
    }

    Handler handler = new ZocoHandler() {
        @Override
        public void onReceive(String result) {
            Toast.makeText(LoginActivity.this, result, Toast.LENGTH_SHORT).show();
            result = result.trim();

            Log.d(TAG, "result : " + result);
            if (result.equals("success")) {  // loginUser positive response
                mPref.putLogin(true);
                startMainActivity();
                Log.d(TAG, "" +
                        "success");
            } else if (result.equals("registered")) { //checkRegister positive response
                Log.d(TAG, "already register success");
                loginUser(nickname, password);
            } else if (result.equals("not registered")) { //checkRegister negative response
                //등록이 안되어 있음. 등록한다.
                Log.d(TAG, "not register");

                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                // 레이아웃 설정
                View layout = inflater.inflate(R.layout.register_dialog_layout, null);

                List<String> list = ReadExcel.readExcel(getApplicationContext());

                ArrayAdapter<String> adapter = new ArrayAdapter<String>(LoginActivity.this,
                        android.R.layout.simple_dropdown_item_1line, list);
                school = (AutoCompleteTextView) layout.findViewById(R.id.register_school);
                school.setAdapter(adapter);
                school.setText(univ);

                TextView emailTextView = (TextView) layout.findViewById(R.id.register_email);
                emailTextView.setText(email);

                nick = (EditText) layout.findViewById(R.id.register_nick_name);
                nick.setText(nickname);


                new AlertDialog.Builder(LoginActivity.this)
                        .setTitle("Register")
                        .setView(layout)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // 예 버튼 눌렀을때 액션 구현
                                nickname = nick.getText().toString();
                                univ = school.getText().toString();
                                Log.d("NARA", "Register popup nickname : " + nickname);
                                Log.d("NARA", "Register popup univ : " + univ);

                                registerUser(email, nickname, provider, univ, password);

                            }
                        })
                        .setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // 아니오 버튼 눌렀을때 액션 구현
                            }
                        }).create().show();

//                ZocoConfirmPopup popup = new ZocoConfirmPopup(LoginActivityTest.this, nickname, univ, email);
//                popup.onCreateDialog(100).show();
//                nickname = popup.getNickName();
//                univ = popup.getUniv();


            } else if (result.equals("cannot login")) { // loginUser negative response
                //로그인이 안됨. 재로그인
                Log.d(TAG, "cannot login");
                loginUser(nickname, password);
            } else if (result.equals("register success")) { // registerUser positive response
                Log.d(TAG, "register success");
                loginUser(nickname, password);
//                ZocoDialog.createConfirmDialog(LoginActivityTest.this, "Is your Information right?", confirmPositiveListener, confirmNegativeListener, createConfirmDialogLayout());
            }
        }
    };


    public void checkRegister(String nickname) {
        Log.d(TAG, "checkRegister URL : " + ZocoNetwork.URL_4_IS_REGISTER + ZocoNetwork.SUFFIX_4_NICKNAME + nickname);
        new ReqTask(getBaseContext(), ZocoNetwork.Method.GET).setHandler(handler).execute(ZocoNetwork.URL_4_IS_REGISTER + ZocoNetwork.SUFFIX_4_NICKNAME + nickname);
    }

    public void registerUser(String email, String nickname, String provider, String univ, String password) {
        User user = new User(email, nickname, provider, univ, password);
        String userdata = new Gson().toJson(user);
        Log.d(TAG, "registerUser URL : " + ZocoNetwork.URL_4_REGISTER);
        new ReqTask(getBaseContext(), ZocoNetwork.Method.POST).setHandler(handler).execute(ZocoNetwork.URL_4_REGISTER, userdata);
    }

    public void loginUser(String nickname, String password) {
        User user = new User(nickname, password);
        String userdata = new Gson().toJson(user);
        Log.d(TAG, "loginUser URL : " + ZocoNetwork.URL_4_REGISTER_BOOK);
        new ReqTask(getBaseContext(), ZocoNetwork.Method.POST).setHandler(handler).execute(ZocoNetwork.URL_4_REGISTER_BOOK, userdata);
    }


}
