package com.zoco.common;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;

import com.zoco.activity.R;

import org.w3c.dom.Text;

import java.util.List;

/**
 * Created by user on 2015-02-25.
 */
public class ZocoConfirmPopup {

    final static int registerDialog = 100;

    Context mContext;
    EditText nick;

    String nickname;
    String email;
    String univ;
    AutoCompleteTextView school;

    public ZocoConfirmPopup(Context context, String nickname, String univ, String email) {
        mContext = context;
        this.nickname = nickname;
        this.univ = univ;
        this.email = email;
    }

    public Dialog onCreateDialog(int id) {
        switch (id) {
            case registerDialog:
                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(mContext.LAYOUT_INFLATER_SERVICE);
                // 레이아웃 설정
                View layout = inflater.inflate(R.layout.register_dialog_layout, null);

                List<String> list = ReadExcel.readExcel(mContext);

                ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext,
                        android.R.layout.simple_dropdown_item_1line, list);
                school = (AutoCompleteTextView) layout.findViewById(R.id.register_school);
                school.setAdapter(adapter);

                TextView emailTextView = (TextView) layout.findViewById(R.id.register_email);
                emailTextView.setText(email);

                nick = (EditText) layout.findViewById(R.id.register_nick_name);
                nick.setText(nickname);

                AlertDialog dialog = new AlertDialog.Builder(mContext)
                        .setTitle("Register")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // 예 버튼 눌렀을때 액션 구현
                                nickname = nick.getText().toString();
                                univ = school.getText().toString();
                                Log.d("NARA", "Register popup nickname : " + nickname);
                                Log.d("NARA", "Register popup univ : " + univ);

                            }
                        })
                        .setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // 아니오 버튼 눌렀을때 액션 구현
                            }
                        }).create();

                // Input 소프트 키보드 보이기
                dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                // AlertDialog에 레이아웃 추가
                dialog.setView(layout);
                return dialog;
        }
        return null;
    }

    public String getNickName() {
        return nickname;
    }

    public String getUniv() {
        return univ;
    }


}
