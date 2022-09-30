package com.yuoxi.android.app.activity.shiwu;


import android.os.Bundle;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.base.utils.CommonUtil;
import com.yuoxi.android.app.R;
import com.yuoxi.android.app.activity.BaseActivity;
import com.yuoxi.android.app.adapter.SWGoldAdapter;
import com.yuoxi.android.app.databinding.ActivitySWGoldBinding;

public class SWGoldActivity extends BaseActivity {

    private ActivitySWGoldBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = getViewData(R.layout.activity_s_w_gold);

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(SWGoldActivity.this));
        binding.recyclerView.setNestedScrollingEnabled(false);
        SWGoldAdapter swGoldAdapter = new SWGoldAdapter(SWGoldActivity.this);
        binding.recyclerView.setAdapter(swGoldAdapter);
        swGoldAdapter.refreshData(CommonUtil.getTitles());
    }

    public void onClickSWHome(View view) {
        openActivity(SWHomeActivity.class);
    }
}