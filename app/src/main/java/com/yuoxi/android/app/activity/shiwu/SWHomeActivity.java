package com.yuoxi.android.app.activity.shiwu;


import android.os.Bundle;

import com.yuoxi.android.app.R;
import com.yuoxi.android.app.activity.BaseActivity;
import com.yuoxi.android.app.adapter.MainPagerAdapter;
import com.yuoxi.android.app.databinding.ActivitySwhomeBinding;
import com.yuoxi.android.app.fragment.SWHomeFragment;

public class SWHomeActivity extends BaseActivity {

    private ActivitySwhomeBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = getViewData(R.layout.activity_swhome);

        MainPagerAdapter mainPagerAdapter = new MainPagerAdapter(getSupportFragmentManager());

        mainPagerAdapter.addFragment("日常任务", SWHomeFragment.getInstance(0));
        mainPagerAdapter.addFragment("新手任务", SWHomeFragment.getInstance(1));

        binding.viewPager.setAdapter(mainPagerAdapter);
        binding.viewPager.setCurrentItem(0);
        binding.viewPager.setOffscreenPageLimit(mainPagerAdapter.getCount());
        binding.tabLayout.setupWithViewPager(binding.viewPager);
    }
}