package com.quakoo.im.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

import com.base.Constants;
import com.base.UserInfo;
import com.base.utils.CommonUtil;
import com.base.utils.MsgCache;
import com.base.utils.PermissionUtils;
import com.base.utils.StatusBarUtil;
import com.quakoo.im.R;
import com.quakoo.im.model.EmotionEntity;

import java.util.ArrayList;
import java.util.List;


public class BaseActivity extends AppCompatActivity {
    public String TAG = this.getClass().getName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBarDarkTheme(false);

    }


    @SuppressLint("MissingSuperCall")
    @Override
    protected void onSaveInstanceState(Bundle outState) {

    }

    public <T extends ViewDataBinding> T getViewData(int layoutId) {
        T binding = DataBindingUtil.setContentView(this, layoutId);
        return binding;
    }

    public void setStatusBarDarkTheme(boolean dark) {
        if (!StatusBarUtil.setStatusBarDarkTheme(this, dark)) {
            StatusBarUtil.setStatusBarColor(this, dark ? R.color.black : R.color.white);
        }
    }

    @SuppressLint("NewApi")
    public void setStatusBarHeight() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            View decorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
            if (findViewById(R.id.status_bar) != null) {
                findViewById(R.id.status_bar).setVisibility(View.VISIBLE);
                findViewById(R.id.status_bar).getLayoutParams().height = CommonUtil.getStatusBarHeight(getApplication());
            }
        }
    }

    public void setUserInfo(UserInfo userInfo) {
        MsgCache.get(this).put(Constants.USER_INFO, userInfo);
    }

    public UserInfo getUserInfo() {
        UserInfo userinfo = (UserInfo) MsgCache.get(this).getAsObject(Constants.USER_INFO);
        if (!CommonUtil.isBlank(userinfo)) {
            return userinfo;
        }
        return new UserInfo();
    }

    public void setEmotionList(EmotionEntity emotionList) {
        MsgCache.get(getApplication()).put(Constants.EMOTIN_LIST + getUserInfo().getId(), emotionList);
    }

    public EmotionEntity getEmotionList() {
        EmotionEntity emotionEntity = (EmotionEntity) MsgCache.get(getApplication()).getAsObject(Constants.EMOTIN_LIST + getUserInfo().getId());
        if (emotionEntity != null && emotionEntity.getEmotionList() != null && emotionEntity.getEmotionList().size() > 0) {
            return emotionEntity;
        } else {
            EmotionEntity emotions = new EmotionEntity();
            List<EmotionEntity.DataBean> list = new ArrayList<>();
            EmotionEntity.DataBean dataBean = new EmotionEntity.DataBean();
            dataBean.setUrl("add");
            dataBean.setStatus(false);
            list.add(dataBean);
            emotions.setEmotionList(list);
            setEmotionList(emotions);
            return emotions;
        }
    }


    public void openActivity(Class<?> mClass) {
        openActivity(mClass, null);
    }

    public void openActivity(Class<?> mClass, int requestCode) {
        Intent intent = new Intent(this, mClass);
        startActivityForResult(intent, requestCode);
    }

    public void openActivity(Class<?> mClass, Bundle mBundle) {
        Intent intent = new Intent(this, mClass);
        if (mBundle != null) {
            intent.putExtras(mBundle);
        }
        startActivity(intent);
    }

    public void openActivity(Class<?> mClass, Bundle mBundle, int requestCode) {
        Intent intent = new Intent(this, mClass);
        if (mBundle != null) {
            intent.putExtras(mBundle);
        }
        startActivityForResult(intent, requestCode);
    }

    private static List<Activity> activityStack = new ArrayList<Activity>();

    public void addActivity(Activity activity) {
        activityStack.add(activity);
    }

    public void removeActivity(Activity activity) {
        activityStack.remove(activity);
    }

    public static void finishActivity(Class mClass) {
        for (int i = 0, size = activityStack.size(); i < size; i++) {
            if (null != activityStack.get(i) && null != mClass
                    && mClass.getSimpleName().equals(activityStack.get(i).getClass().getSimpleName())) {
                activityStack.get(i).finish();
            }
        }
    }

    public void finishAllActivity() {
        for (int i = 0, size = activityStack.size(); i < size; i++) {
            if (null != activityStack.get(i)) {
                activityStack.get(i).finish();
            }
        }
        activityStack.clear();
    }

    public boolean checkPermissions(String type, int code) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            boolean isAllGranted = PermissionUtils.checkPermissionAllGranted(this, type);
            if (!isAllGranted) {
                PermissionUtils.requestPermissions(this, type, code);
                return false;
            }
        }
        return true;
    }

}
