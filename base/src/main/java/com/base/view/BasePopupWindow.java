package com.base.view;

import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.drawable.ColorDrawable;
import android.os.IBinder;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;

import com.base.utils.NavigationBar;

public abstract class BasePopupWindow extends PopupWindow {

    protected Context context;
    private WindowManager windowManager;
    private View maskView;

    public BasePopupWindow(Context context) {
        super(context);
        this.context = context;
        windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        setContentView(initContentView());
        setWidth(initWidth());
        setHeight(initHeight());
        setFocusable(true);
        setTouchable(true);
        setOutsideTouchable(true);
        setBackgroundDrawable(new ColorDrawable());
        setAnimationStyle(animationStyle());
        setClippingEnabled(false);
    }

    protected abstract int animationStyle();

    protected abstract View initContentView();

    protected abstract int initWidth();

    protected abstract int initHeight();

    @Override
    public void showAsDropDown(View anchor) {
        addMask(anchor.getWindowToken());
        super.showAsDropDown(anchor);
    }

    @Override
    public void showAsDropDown(View anchor, int xoff, int yoff, int gravity) {
        addMask(anchor.getWindowToken());
        super.showAsDropDown(anchor, xoff, yoff, gravity);
    }

    @Override
    public void showAtLocation(View anchor, int gravity, int x, int y) {
        addMask(anchor.getWindowToken());
        y = NavigationBar.getNavigationBarHeightIfRoom(context);
        super.showAtLocation(anchor, gravity, x, y);
    }

    private void addMask(IBinder token) {
        WindowManager.LayoutParams wl = new WindowManager.LayoutParams();
        wl.width = WindowManager.LayoutParams.MATCH_PARENT;
        wl.height = WindowManager.LayoutParams.MATCH_PARENT;
        wl.format = PixelFormat.TRANSLUCENT;//不设置这个弹出框的透明遮罩显示为黑色
        wl.type = WindowManager.LayoutParams.TYPE_APPLICATION_PANEL;//该Type描述的是形成的窗口的层级关系
        wl.token = token;//获取当前Activity中的View中的token,来依附Activity
        maskView = new View(context);
        maskView.setBackgroundColor(0x7f000000);
        maskView.setFitsSystemWindows(false);
        maskView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    removeMask();
                    return true;
                }
                return false;
            }
        });
        /**
         * 通过WindowManager的addView方法创建View，产生出来的View根据WindowManager.LayoutParams属性不同，效果也就不同了。
         * 比如创建系统顶级窗口，实现悬浮窗口效果！
         */
        windowManager.addView(maskView, wl);
    }

    private void removeMask() {
        if (null != maskView) {
            try {
                windowManager.removeViewImmediate(maskView);
            } catch (Exception e) {
                e.getMessage();
            } finally {
                maskView = null;
            }
        }
    }

    @Override
    public void dismiss() {
        removeMask();
        super.dismiss();
    }
}
