package com.yuoxi.android.app;

import com.base.BaseApplication;
import com.base.utils.LogUtil;
import com.okhttp.utils.HttpsUtils;
import com.okhttp.utils.OkHttpUtils;

import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

public class MainApplication extends BaseApplication {

    @Override
    public void onCreate() {
        super.onCreate();

        initOkHttp();
    }

    private void initOkHttp() {
        HttpsUtils.SSLParams sslParams = HttpsUtils.getSslSocketFactory(null, null, null);
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(20000L, TimeUnit.MILLISECONDS)
                .readTimeout(20000L, TimeUnit.MILLISECONDS)
                .addInterceptor(new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {

                    @Override
                    public void log(String message) {
                        LogUtil.i("message", message);
                    }
                }).setLevel(HttpLoggingInterceptor.Level.BODY))
                .hostnameVerifier(new HostnameVerifier() {

                    @Override
                    public boolean verify(String hostname, SSLSession session) {
                        return true;
                    }
                })
                .sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager)
                .build();
        OkHttpUtils.initClient(okHttpClient);
    }
}
