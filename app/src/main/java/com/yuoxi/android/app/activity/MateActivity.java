package com.yuoxi.android.app.activity;


import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.yuoxi.android.app.R;
import com.yuoxi.android.app.databinding.ActivityMateBinding;

public class MateActivity extends BaseActivity {


    private ActivityMateBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = getViewData(R.layout.activity_mate);
        setStatusBarHeight();

    }

    public void onClickMate(View view) {
        TextView mateView = (TextView) view;
        view.setSelected(!view.isSelected());
        view.setBackgroundResource(view.isSelected() ? R.drawable.button_cancel : R.drawable.button_mate);
        mateView.setText(view.isSelected() ? "取消" : "开始匹配");
    }

    public void onClickBack(View view) {
        finish();
    }
}