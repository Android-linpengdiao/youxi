package com.base.view;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.view.View;

import androidx.annotation.NonNull;

import com.base.R;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;

public abstract class BaseBottomSheetDialog extends BottomSheetDialog {

    public Context context;
    private BottomSheetBehavior mDialogBehavior;
    private static float slideOffset = 0;

    public BaseBottomSheetDialog(Context context) {
        super(context, R.style.BottomSheetDialog);
        this.context = context;
        View rootView = initContentView();
        setContentView(rootView);
        setCanceledOnTouchOutside(false);

        mDialogBehavior = BottomSheetBehavior.from((View) rootView.getParent());
        mDialogBehavior.setPeekHeight(getWindowHeight());
        //禁止滑动关闭但允许点击背景关闭
        mDialogBehavior.setHideable(false);
        //dialog滑动监听
        mDialogBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                    mDialogBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                } else if (newState == BottomSheetBehavior.STATE_SETTLING) {
                    if (slideOffset <= -0.28) {
                        //当向下滑动时 值为负数
                        dismiss();
                    }
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float _slideOffset) {
            }
        });

    }

    public void setPeekHeight(int height) {
        mDialogBehavior.setPeekHeight(height);

    }

    protected abstract View initContentView();

    private int getWindowHeight() {
        Resources res = context.getResources();
        DisplayMetrics displayMetrics = res.getDisplayMetrics();
        return displayMetrics.heightPixels;
    }
}
