package com.yuoxi.android.app.activity.shiwu;


import android.os.Bundle;
import android.view.View;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.base.utils.CommonUtil;
import com.base.view.OnClickListener;
import com.yuoxi.android.app.R;
import com.yuoxi.android.app.activity.BaseActivity;
import com.yuoxi.android.app.activity.FeedbackActivity;
import com.yuoxi.android.app.activity.PlayerActivity;
import com.yuoxi.android.app.adapter.AJJuBenVerticalAdapter;
import com.yuoxi.android.app.adapter.GameRuleAdapter;
import com.yuoxi.android.app.databinding.ActivitySWGameRuleBinding;

public class SWGameRuleActivity extends BaseActivity {

    private ActivitySWGameRuleBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = getViewData(R.layout.activity_s_w_game_rule);

        binding.recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        GameRuleAdapter gameRuleAdapter = new GameRuleAdapter(this);
        binding.recyclerView.setAdapter(gameRuleAdapter);
        gameRuleAdapter.refreshData(CommonUtil.getTitles());
        gameRuleAdapter.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view, Object object) {
                openActivity(PlayerActivity.class);

            }

            @Override
            public void onLongClick(View view, Object object) {

            }
        });
    }

    public void onClickFeedback(View view) {
        openActivity(FeedbackActivity.class);
    }

    public void onClickBack(View view) {
        finish();
    }
}