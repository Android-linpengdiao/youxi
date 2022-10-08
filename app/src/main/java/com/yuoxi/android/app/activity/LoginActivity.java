package com.yuoxi.android.app.activity;

import android.os.Bundle;
import android.view.View;

import com.base.BaseApplication;
import com.base.UserInfo;
import com.base.manager.LoadingManager;
import com.base.utils.ToastUtils;
import com.okhttp.ResultClient;
import com.okhttp.SendRequest;
import com.okhttp.callbacks.GenericsCallback;
import com.okhttp.sample_okhttp.JsonGenericsSerializator;
import com.quakoo.im.aiyou.ImSocketService;
import com.yuoxi.android.app.MainActivity;
import com.yuoxi.android.app.MainApplication;
import com.yuoxi.android.app.R;
import com.yuoxi.android.app.databinding.ActivityLoginBinding;

import okhttp3.Call;
import okhttp3.Request;
import xyz.doikki.videoplayer.util.PlayerUtils;

public class LoginActivity extends BaseActivity {

    private ActivityLoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = getViewData(R.layout.activity_login);
        setStatusBarHeight();
        finishAllActivity();
        addActivity(this);


    }

    public void onClickLogin(View view) {
        login();
    }

    private void login() {
        SendRequest.login("19920026487", "123456",
                new GenericsCallback<ResultClient<UserInfo>>(new JsonGenericsSerializator()) {

                    @Override
                    public void onBefore(Request request, int id) {
                        super.onBefore(request, id);
                        LoadingManager.showLoadingDialog(LoginActivity.this);
                    }

                    @Override
                    public void onAfter(int id) {
                        super.onAfter(id);
                        LoadingManager.hideLoadingDialog(LoginActivity.this);
                    }

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        if (PlayerUtils.getNetworkType(getApplicationContext()) == PlayerUtils.NO_NETWORK) {
                            ToastUtils.showShort(getApplicationContext(), "当前网络异常，请检查网络");
                        }
                    }

                    @Override
                    public void onResponse(ResultClient<UserInfo> response, int id) {
                        if (response.isSuccess() && response.getData() != null) {
                            MainApplication.getInstance().setUserInfo(response.getData());
                            ImSocketService.startImService(BaseApplication.getInstance()); //开启长链接
                            openActivity(MainActivity.class);
                            ToastUtils.showShort(getApplicationContext(), "登录成功");
                            finish();
                        } else {
                            ToastUtils.showShort(getApplicationContext(), response.getMsg());
                        }
                    }
                });

    }
}