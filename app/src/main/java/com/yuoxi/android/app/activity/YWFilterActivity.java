package com.yuoxi.android.app.activity;


import android.content.Context;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.base.utils.CommonUtil;
import com.base.view.OnMultiClickListener;
import com.base.view.RecycleViewDivider;
import com.yuoxi.android.app.R;
import com.yuoxi.android.app.adapter.YWLinearHorizontalAdapter;
import com.yuoxi.android.app.adapter.YuewanLinearVerticalAdapter;
import com.yuoxi.android.app.databinding.ActivityYWFilterBinding;

public class YWFilterActivity extends BaseActivity {

    private ActivityYWFilterBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = getViewData(R.layout.activity_y_w_filter);

        RecycleViewDivider horizontalDivider = new RecycleViewDivider(this,
                LinearLayoutManager.HORIZONTAL,
                CommonUtil.dip2px(this, 10),
                Color.parseColor("#181526"));
        binding.horizontalRecyclerView.addItemDecoration(horizontalDivider);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        binding.horizontalRecyclerView.setLayoutManager(layoutManager);
        YWLinearHorizontalAdapter ywLinearHorizontalAdapter = new YWLinearHorizontalAdapter(this);
        binding.horizontalRecyclerView.setAdapter(ywLinearHorizontalAdapter);
        ywLinearHorizontalAdapter.refreshData(CommonUtil.getTitles());

        RecycleViewDivider verticalDivider = new RecycleViewDivider(YWFilterActivity.this,
                LinearLayoutManager.VERTICAL,
                CommonUtil.dip2px(YWFilterActivity.this, 10),
                Color.parseColor("#181526"));
        binding.verticalRecyclerView.addItemDecoration(verticalDivider);
        binding.verticalRecyclerView.setLayoutManager(new LinearLayoutManager(YWFilterActivity.this));
        YuewanLinearVerticalAdapter yuewanLinearVerticalAdapter = new YuewanLinearVerticalAdapter(YWFilterActivity.this);
        binding.verticalRecyclerView.setAdapter(yuewanLinearVerticalAdapter);
        yuewanLinearVerticalAdapter.refreshData(CommonUtil.getTitles());

        binding.sortView.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void OnMultiClick(View view) {
                openPopupWindow();
            }
        });
    }


    private PopupWindow mWindow;
    private WindowManager windowManager;
    private View maskView;

    private void openPopupWindow() {
        View view = binding.sortView;
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        View contentView = LayoutInflater.from(YWFilterActivity.this).inflate(R.layout.view_yw_filter_popup_layout, null);
        TextView titleView = contentView.findViewById(R.id.titleView);

        mWindow = new PopupWindow(contentView,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                false);
        mWindow.setClippingEnabled(false);

        int x = 0;
        int y = location[1] + getResources().getDimensionPixelSize(R.dimen.dp_32);
        mWindow.showAtLocation(getWindow().getDecorView(), Gravity.NO_GRAVITY, x, y);
        titleView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mWindow != null) {
                    mWindow.dismiss();
                    mWindow = null;
                }
                if (null != maskView && windowManager != null) {
                    try {
                        windowManager.removeViewImmediate(maskView);
                        maskView = null;
                    } catch (Exception e) {
                        e.getMessage();
                    } finally {
                        maskView = null;
                    }
                }
            }
        });
        windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        WindowManager.LayoutParams wl = new WindowManager.LayoutParams();
        wl.width = WindowManager.LayoutParams.MATCH_PARENT;
        wl.height = WindowManager.LayoutParams.MATCH_PARENT;
        wl.format = PixelFormat.TRANSLUCENT;
        wl.type = WindowManager.LayoutParams.TYPE_APPLICATION_PANEL;
        wl.token = view.getWindowToken();
        maskView = new View(YWFilterActivity.this);
//        maskView.setBackgroundColor(Color.parseColor("#33191F2D"));
        maskView.setFitsSystemWindows(false);
        maskView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
//                    removeMask();
                    return true;
                }
                return false;
            }
        });
        windowManager.addView(maskView, wl);
        maskView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mWindow != null) {
                    mWindow.dismiss();
                    mWindow = null;
                }
                if (null != maskView && windowManager != null) {
                    try {
                        windowManager.removeViewImmediate(maskView);
                        maskView = null;
                    } catch (Exception e) {
                        e.getMessage();
                    } finally {
                        maskView = null;
                    }
                }
            }
        });
    }


}