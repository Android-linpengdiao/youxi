package com.base;

import android.app.Application;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import com.base.utils.CommonUtil;
import com.base.utils.MsgCache;

public class BaseApplication extends Application {

    private static BaseApplication myApplication;
    private static Handler handler;

    @Override
    public void onCreate() {
        super.onCreate();
        myApplication = this;
        handler = new Handler(Looper.getMainLooper());
//        initAssets();
    }

    public static BaseApplication getInstance() {
        return myApplication;
    }

    public static Handler getHandler() {
        return handler;
    }

    private Typeface typeface;
    private Typeface typefaceBold;

    public Typeface getTypeface() {
        if (typeface == null) {
            return Typeface.createFromAsset(getAssets(), "fonts/FZBIAOYSJW_GBK.TTF");
        } else {
            return typeface;
        }
    }

    public Typeface getTypefaceBold() {
        if (typefaceBold == null) {
            return Typeface.createFromAsset(getAssets(), "fonts/FZTYH_GBK.TTF");
        } else {
            return typefaceBold;
        }
    }


    private void initAssets() {
        typeface = Typeface.createFromAsset(getAssets(), "fonts/FZBIAOYSJW_GBK.TTF");
        typefaceBold = Typeface.createFromAsset(getAssets(), "fonts/FZTYH_GBK.TTF");
    }

    public void setUserInfo(UserInfo userInfo) {
        MsgCache.get(this).put(Constants.USER_INFO, userInfo);
    }

    public static UserInfo getUserInfo() {
        UserInfo userinfo = (UserInfo) MsgCache.get(getInstance()).getAsObject(Constants.USER_INFO);
        if (!CommonUtil.isBlank(userinfo)) {
            return userinfo;
        }
        return new UserInfo();
    }

    public String getMetaData(String property) {
        String out = "";

        if (property.equals("versionCode")) {
            try {
                PackageInfo pi = getPackageManager().getPackageInfo(getPackageName(), 0);
                return String.valueOf(pi.versionCode);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (property.equals("versionName")) {
            try {
                PackageInfo pi = getPackageManager().getPackageInfo(getPackageName(), 0);
                return pi.versionName;
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (property.equals("CHANNEL")) {
            try {
                ApplicationInfo ai = getPackageManager()
                        .getApplicationInfo(getPackageName(),
                                PackageManager.GET_META_DATA);
                Bundle b = ai.metaData;
                return (String) b.get("CHANNEL");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return out;
    }
}
