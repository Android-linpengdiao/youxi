package com.base.view;

import android.view.View;
import android.widget.AdapterView;

import java.util.Calendar;

/**
 * 防止控件快速点击
 */
public abstract class OnMultiClickListener implements AdapterView.OnClickListener {

    public static final int MIN_CLICK_DELAY_TIME = 1000;
    private long lastClickTime = 0;

    @Override
    public void onClick(View view) {
        long currentTime = Calendar.getInstance().getTimeInMillis();
        if (currentTime - lastClickTime > MIN_CLICK_DELAY_TIME) {
            lastClickTime = currentTime;
            OnMultiClick(view);
        }
    }

    public abstract void OnMultiClick(View view);
}
