package com.base.utils;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.base.BaseApplication;
import com.base.R;

public class ToastUtils {
    private final static String TAG = "ToastUtils";

    public static void showShort(Context context, int resId) {
        String msg;
        try {
            msg = context.getResources().getString(resId);
        } catch (Exception e) {
            msg = TAG + ": " + e.getMessage();
        }
        showShort(context, msg);
    }

    public static void showShort(final Context context, final String msg) {
        if (null == context || null == msg || msg.equals("") || msg.length() == 0) {
            return;
        }
        BaseApplication.getHandler().post(new Runnable() {
            @Override
            public void run() {
//                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();

                Toast toast = new Toast(context);
                View view = LayoutInflater.from(context).inflate(R.layout.toast_custom, null);
                TextView content = view.findViewById(R.id.content);
                content.setText(msg);
                toast.setView(view);
                toast.setDuration(Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }
        });
    }

    public static void showLong(Context context, int resId) {
        String msg;
        try {
            msg = context.getResources().getString(resId);
        } catch (Exception e) {
            msg = TAG + ": " + e.getMessage();
        }
        showShort(context, msg);
    }

    public static void showLong(final Context context, final String msg) {
        if (null == context || null == msg || msg.equals("") || msg.length() == 0) {
            return;
        }
        BaseApplication.getHandler().post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
            }
        });
    }
}
