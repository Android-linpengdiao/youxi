package com.yuoxi.android.app.activity;


import android.os.Bundle;
import android.view.View;

import androidx.viewpager.widget.ViewPager;

import com.yuoxi.android.app.R;
import com.yuoxi.android.app.adapter.MainPagerAdapter;
import com.yuoxi.android.app.databinding.ActivityBangRuleBinding;
import com.yuoxi.android.app.fragment.BangRuleFragment;
import com.yuoxi.android.app.fragment.JBHomeChildFragment;

public class BangRuleActivity extends BaseActivity {

    private ActivityBangRuleBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = getViewData(R.layout.activity_bang_rule);
        setStatusBarHeight();

        MainPagerAdapter mainPagerAdapter = new MainPagerAdapter(getSupportFragmentManager());

        mainPagerAdapter.addFragment("剧本榜", BangRuleFragment.getInstance(0));
        mainPagerAdapter.addFragment("高玩榜", BangRuleFragment.getInstance(1));
        mainPagerAdapter.addFragment("财富榜", BangRuleFragment.getInstance(2));
        mainPagerAdapter.addFragment("事务榜", BangRuleFragment.getInstance(3));

        binding.viewPager.setAdapter(mainPagerAdapter);
        binding.viewPager.setCurrentItem(0);
        binding.viewPager.setOffscreenPageLimit(mainPagerAdapter.getCount());
        binding.tabLayout.setupWithViewPager(binding.viewPager);
        binding.viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    public void onClickBack(View view) {
        finish();
    }
}