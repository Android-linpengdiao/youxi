package com.base.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.base.BaseApplication;

public class SharedPreferencesUtils {

    public static String SP_DATA = "sp_data";
    public static String updateSongTest = "updateSongTest";

    public static SharedPreferencesUtils mInstance;
    public static SharedPreferences sharedPreferences;

    public static SharedPreferencesUtils getInstance() {
        if (mInstance == null) {
            synchronized (SharedPreferencesUtils.class) {
                if (mInstance == null) {
                    mInstance = new SharedPreferencesUtils();
                }
            }
        }
        return mInstance;
    }

    public SharedPreferencesUtils() {
        sharedPreferences = BaseApplication.getInstance().getSharedPreferences(SP_DATA, Context.MODE_PRIVATE);
    }


    public int getPlayIndex(long uid, int vid) {
        return sharedPreferences.getInt("playIndex" + uid + vid, 0);
    }

    public void setPlayIndex(long uid, int vid, int index) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("playIndex" + uid + vid, index);
        editor.commit();
    }


    public boolean getPlayOnMobileNetwork() {
        return sharedPreferences.getBoolean("playOnMobileNetwork", false);
    }

    public void setPlayOnMobileNetwork(boolean playOnMobileNetwork) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("playOnMobileNetwork", playOnMobileNetwork);
        editor.commit();
    }

    public int getTextSize() {
        return sharedPreferences.getInt("textSize", 1);
    }

    /**
     * @param value 0-小号 1-标准 2-中号 3-大号 4-特大号
     */
    public void setTextSize(int value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("textSize", value);
        editor.commit();
    }

}
