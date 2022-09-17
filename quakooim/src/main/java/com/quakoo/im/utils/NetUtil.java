package com.quakoo.im.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by Administrator on 2018/3/13.
 */

public class NetUtil {

    /**
     * 没有连接网络
     */
    private static final int NETWORK_NONE = -1;
    /**
     * 移动网络
     */
    private static final int NETWORK_MOBILE = 0;
    /**
     * 无线网络
     */
    private static final int NETWORK_WIFI = 1;

    public static boolean getNetWork(Context context) {
        // 得到连接管理器对象
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetworkInfo = connectivityManager
                .getActiveNetworkInfo();
        if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
            if (activeNetworkInfo.getType() == (ConnectivityManager.TYPE_WIFI)) {
                return true;
            } else if (activeNetworkInfo.getType() == (ConnectivityManager.TYPE_MOBILE)) {
                return true;
            }
        } else {
            return false;
        }
        return false;
    }

    public static int getNetWorkState(Context context) {
        // 得到连接管理器对象
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetworkInfo = connectivityManager
                .getActiveNetworkInfo();
        if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
            if (activeNetworkInfo.getType() == (ConnectivityManager.TYPE_WIFI)) {
//                UniteUpdateDataModel model = new UniteUpdateDataModel();
//                model.setKey(Constants.NETWORK_WIFI);
//                EventBus.getDefault().postSticky(model);
                return NETWORK_WIFI;
            } else if (activeNetworkInfo.getType() == (ConnectivityManager.TYPE_MOBILE)) {
//                UniteUpdateDataModel model = new UniteUpdateDataModel();
//                model.setKey(Constants.NETWORK_MOBILE);
//                EventBus.getDefault().postSticky(model);
                return NETWORK_MOBILE;
            }
        } else {
//            UniteUpdateDataModel model = new UniteUpdateDataModel();
//            model.setKey(Constants.NETWORK_NONE);
//            EventBus.getDefault().postSticky(model);
            return NETWORK_NONE;
        }
//        UniteUpdateDataModel model = new UniteUpdateDataModel();
//        model.setKey(Constants.NETWORK_NONE);
//        EventBus.getDefault().postSticky(model);
        return NETWORK_NONE;
    }

}
