package com.yuoxi.android.app;


import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.widget.RadioGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.base.Constants;
import com.base.MessageBus;
import com.base.utils.MsgCache;
import com.base.utils.ToastUtils;
import com.next.easynavigation.view.EasyNavigationBar;
import com.quakoo.im.IMSharedPreferences.ImSharedPreferences;
import com.quakoo.im.aiyou.ImSocketService;
import com.yuoxi.android.app.activity.BaseActivity;
import com.yuoxi.android.app.activity.LoginActivity;
import com.yuoxi.android.app.databinding.ActivityMainBinding;
import com.yuoxi.android.app.fragment.MainHomeFragment;
import com.yuoxi.android.app.fragment.MainMessageFragment;
import com.yuoxi.android.app.fragment.MainMineFragment;
import com.yuoxi.android.app.fragment.MainYuewanFragment;
import com.yuoxi.android.app.view.ViewUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends BaseActivity implements RadioGroup.OnCheckedChangeListener {

    private ActivityMainBinding binding;
    private FragmentManager mFragmentManager;
    private Fragment mCurrentFragment;

//    private List<Fragment> fragmentList = new ArrayList<>();
//    private String[] strings = {"首页", "约玩", "消息", "我的"};
//    private int[] unSelectList = {R.mipmap.main_home_f, R.mipmap.main_yuewan_f, R.mipmap.main_message_f, R.mipmap.main_mine_f};
//    private int[] selectList = {R.mipmap.main_home_t, R.mipmap.main_yuewan_t, R.mipmap.main_message_t, R.mipmap.main_mine_t};


    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = getViewData(R.layout.activity_main);
        finishAllActivity();
        addActivity(this);

        Drawable drawableHome = getResources().getDrawable(R.drawable.main_home);
        drawableHome.setBounds(0, 0, (int) getResources().getDimension(R.dimen.dp_22), (int) getResources().getDimension(R.dimen.dp_22));
        binding.radioButtonHome.setCompoundDrawables(null, drawableHome, null, null);
        Drawable drawableYueWan = getResources().getDrawable(R.drawable.main_yuewan);
        drawableYueWan.setBounds(0, 0, (int) getResources().getDimension(R.dimen.dp_22), (int) getResources().getDimension(R.dimen.dp_22));
        binding.radioButtonYueWan.setCompoundDrawables(null, drawableYueWan, null, null);
        Drawable drawableMessage = getResources().getDrawable(R.drawable.main_message);
        drawableMessage.setBounds(0, 0, (int) getResources().getDimension(R.dimen.dp_22), (int) getResources().getDimension(R.dimen.dp_22));
        binding.radioButtonMessage.setCompoundDrawables(null, drawableMessage, null, null);
        Drawable drawableMine = getResources().getDrawable(R.drawable.main_mine);
        drawableMine.setBounds(0, 0, (int) getResources().getDimension(R.dimen.dp_22), (int) getResources().getDimension(R.dimen.dp_22));
        binding.radioButtonMine.setCompoundDrawables(null, drawableMine, null, null);

        binding.radioGroupView.setOnCheckedChangeListener(this);
        initDefaultFragment();

//        fragmentList.add(new MainHomeFragment());
//        fragmentList.add(new MainYuewanFragment());
//        fragmentList.add(new MainMessageFragment());
//        fragmentList.add(new MainMineFragment());
//
//        binding.easyNavigationBar
//                .titleItems(strings)//导航栏文字
//                .selectIconItems(selectList)//选中的图片
//                .normalIconItems(unSelectList)//未选中的图片
//                .selectTextColor(Color.parseColor("#8654FF"))//选中的文字颜色
//                .normalTextColor(Color.parseColor("#FFFFFF"))//未选中的文字颜色
//                .textSizeType(TypedValue.COMPLEX_UNIT_PX)
//                .tabTextSize(getResources().getDimensionPixelSize(R.dimen.sp_10))
//
//                .lineColor(getResources().getColor(R.color.dividerColor))
//
//                .canScroll(false)//可以滑动
//                .mode(EasyNavigationBar.NavigationMode.MODE_NORMAL)
//                .navigationBackground(getResources().getColor(R.color.colorPrimary))
//                .fragmentList(fragmentList)//存放fragment的集合
//                .fragmentManager(getSupportFragmentManager())//fragment管理器
//                .setOnTabClickListener(new EasyNavigationBar.OnTabClickListener() {
//                    @Override
//                    public boolean onTabSelectEvent(View view, int position) {
//                        return false;
//                    }
//
//                    @Override
//                    public boolean onTabReSelectEvent(View view, int position) {
//                        return false;
//                    }
//                })
//                .setOnTabLoadListener(new EasyNavigationBar.OnTabLoadListener() {
//                    @Override
//                    public void onTabLoadCompleteEvent() {
//
//                    }
//                })
//                .build();

        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }

    }

    private void initDefaultFragment() {
        mFragmentManager = getSupportFragmentManager();
        mCurrentFragment = ViewUtils.createFragment(MainHomeFragment.class, true);
        mFragmentManager.beginTransaction().add(R.id.content_frame, mCurrentFragment).commit();
    }

    public Fragment replaceContentFragment(Class<?> mClass) {
        Fragment fragment = ViewUtils.createFragment(mClass, true);
        if (fragment.isAdded()) {
            mFragmentManager.beginTransaction().hide(mCurrentFragment).show(fragment).commitAllowingStateLoss();
        } else {
            mFragmentManager.beginTransaction().hide(mCurrentFragment).add(R.id.content_frame, fragment).commitAllowingStateLoss();
        }
        mCurrentFragment = fragment;
        return mCurrentFragment;
    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
        switch (checkedId) {
            case R.id.radioButtonHome:
                replaceContentFragment(MainHomeFragment.class);
                break;
            case R.id.radioButtonYueWan:
                replaceContentFragment(MainYuewanFragment.class);
                break;
            case R.id.radioButtonMessage:
                replaceContentFragment(MainMessageFragment.class);
                break;
            case R.id.radioButtonMine:
                replaceContentFragment(MainMineFragment.class);

                break;
            default:
                break;
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
            MsgCache.get(getApplication()).remove(Constants.USER_INFO); //清除缓存里的帐号信息
            stopService(new Intent(MainActivity.this, ImSocketService.class));
            ImSharedPreferences.exitLogin();
            openActivity(LoginActivity.class);

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