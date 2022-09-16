package com.yuoxi.android.app.activity;

import android.os.Bundle;

import com.yuoxi.android.app.R;
import com.yuoxi.android.app.databinding.ActivityBlacklistBinding;

public class BlacklistActivity extends BaseActivity {

    private ActivityBlacklistBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = getViewData(R.layout.activity_blacklist);
    }
}