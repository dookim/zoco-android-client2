package com.zoco.common;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by user on 2015-02-17.
 */
public class ZocoDialog extends AlertDialog {
    protected ZocoDialog(Context context) {
        super(context);
    }

    protected ZocoDialog(Context context, int theme) {
        super(context, theme);
    }

    protected ZocoDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    public static AlertDialog createSingleChoiceDialog(Context context, CharSequence title,
                                                       OnClickListener positiveListener, OnClickListener negativeListener,
                                                       OnClickListener selectListener, ArrayList<String> list) {
        return new AlertDialog.Builder(context).setTitle(title)
                .setSingleChoiceItems(list.toArray(new String[list.size()]), 0, selectListener)
                .setPositiveButton(android.R.string.ok, positiveListener)
                .setNegativeButton(android.R.string.cancel, negativeListener)
                .create();
    }

    public static AlertDialog createConfirmDialog(Context context, CharSequence title,
                                                  OnClickListener positiveListener, OnClickListener negativeListener,
                                                  View layout) {
        return new AlertDialog.Builder(context).setTitle(title)
                .setView(layout)
                .setPositiveButton(android.R.string.ok, positiveListener)
                .setNegativeButton(android.R.string.cancel, negativeListener)
                .create();
    }


    public static void showToast(Context context, String title, boolean isShortPeriod) {
        if (isShortPeriod) {
            Toast.makeText(context, title, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, title, Toast.LENGTH_LONG).show();
        }
    }

//    private Context mContext;
//    ArrayList<String> mList;
//
//    public ZocoDialog(Context context) {
//        mContext = context;
//    }
//
//    public void setDialog(ArrayList<String> list) {
//        AlertDialog.Builder ab = new AlertDialog.Builder(mContext);
//        ab.setTitle("Select account");
//        ab.setSingleChoiceItems(list.toArray(new String[list.size()]), 0,
//                new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int whichButton) {
//                        switch(whichButton){
//                            case 1:
//
//                        }
//                    }
//                }).setPositiveButton("Ok",
//                new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int whichButton) {
//
//                    }
//                }).setNegativeButton("Cancel",
//                new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int whichButton) {
//                        // Cancel 버튼 클릭시
//                    }
//                });
//        ab.show();
//    }

}
