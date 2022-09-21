package com.yuoxi.android.app;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

import androidx.fragment.app.Fragment;

import com.base.MessageBus;
import com.base.utils.LogUtil;
import com.base.utils.ToastUtils;
import com.next.easynavigation.view.EasyNavigationBar;
import com.yuoxi.android.app.activity.BaseActivity;
import com.yuoxi.android.app.databinding.ActivityMainBinding;
import com.yuoxi.android.app.fragment.MainHomeFragment;
import com.yuoxi.android.app.fragment.MainMessageFragment;
import com.yuoxi.android.app.fragment.MainMineFragment;
import com.yuoxi.android.app.fragment.MainYuewanFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity {

    private ActivityMainBinding binding;

    private List<Fragment> fragmentList = new ArrayList<>();
    private String[] strings = {"首页", "约玩", "消息", "我的"};
    private int[] unSelectList = {R.mipmap.main_home_t, R.mipmap.main_home_t, R.mipmap.main_home_t, R.mipmap.main_home_t};
    private int[] selectList = {R.mipmap.main_home_t, R.mipmap.main_home_t, R.mipmap.main_home_t, R.mipmap.main_home_t};


    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = getViewData(R.layout.activity_main);
        finishAllActivity();
        addActivity(this);

        fragmentList.add(new MainHomeFragment());
        fragmentList.add(new MainYuewanFragment());
        fragmentList.add(new MainMessageFragment());
        fragmentList.add(new MainMineFragment());

        binding.easyNavigationBar
                .titleItems(strings)//导航栏文字
                .selectIconItems(selectList)//选中的图片
                .normalIconItems(unSelectList)//未选中的图片
                .selectTextColor(Color.parseColor("#8654FF"))//选中的文字颜色
                .normalTextColor(Color.parseColor("#FFFFFF"))//未选中的文字颜色
//                .textSizeType(EasyNavigationBar.TextSizeType.TYPE_SP)
//                .tabTextSize(getResources().getDimensionPixelSize(R.dimen.sp_10))
                .tabTextSize(10)

                .navigationHeight(56)
                .lineHeight(1)
                .lineColor(getResources().getColor(R.color.colorPrimary))

                .canScroll(false)//可以滑动
                .mode(EasyNavigationBar.NavigationMode.MODE_NORMAL)
                .navigationBackground(getResources().getColor(R.color.colorPrimary))
                .fragmentList(fragmentList)//存放fragment的集合
                .fragmentManager(getSupportFragmentManager())//fragment管理器
                .setOnTabClickListener(new EasyNavigationBar.OnTabClickListener() {
                    @Override
                    public boolean onTabSelectEvent(View view, int position) {
                        return false;
                    }

                    @Override
                    public boolean onTabReSelectEvent(View view, int position) {
                        return false;
                    }
                })
                .setOnTabLoadListener(new EasyNavigationBar.OnTabLoadListener() {
                    @Override
                    public void onTabLoadCompleteEvent() {

                    }
                })
                .build();

        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(final MessageBus messageBus) {
        if (messageBus.getCodeType().equals(MessageBus.msgId_logout)) {
            EventBus.getDefault().unregister(this);
            String content = (int) messageBus.getMessage() == 400
                    ? getString(R.string.abnormal)
                    : "您的账号已被冻结";
            ToastUtils.showShort(getApplicationContext(), "您的账号在其他设备登录,如果这不是您的操作,请及时修改您的登录密码。");
//            openActivity(LoginActivity.class);

        }
    }

    private long lastTime = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.i(TAG, "onKeyDown: ");
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (System.currentTimeMillis() - lastTime >= 3000) {
                lastTime = System.currentTimeMillis();
                ToastUtils.showShort(MainActivity.this, "再按一次退出应用");
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
}