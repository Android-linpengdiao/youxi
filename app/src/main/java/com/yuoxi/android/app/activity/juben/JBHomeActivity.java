package com.yuoxi.android.app.activity.juben;


import android.os.Bundle;
import android.view.View;

import androidx.viewpager.widget.ViewPager;

import com.yuoxi.android.app.R;
import com.yuoxi.android.app.activity.BaseActivity;
import com.yuoxi.android.app.activity.anjian.AJHomeActivity;
import com.yuoxi.android.app.adapter.MainPagerAdapter;
import com.yuoxi.android.app.databinding.ActivityJBHomeBinding;
import com.yuoxi.android.app.fragment.AJHomeFragment;
import com.yuoxi.android.app.fragment.JBHomeFragment;

public class JBHomeActivity extends BaseActivity {

    private ActivityJBHomeBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = getViewData(R.layout.activity_j_b_home);
        setStatusBarHeight();

        MainPagerAdapter mainPagerAdapter = new MainPagerAdapter(getSupportFragmentManager());

        mainPagerAdapter.addFragment("游玩区", JBHomeFragment.getInstance(0));
        mainPagerAdapter.addFragment("预约区", JBHomeFragment.getInstance(1));

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

    public void onClickAddRoom(View view) {
        openActivity(AJHomeActivity.class);
    }

    public void onClickSearch(View view) {
        openActivity(JBSearchActivity.class);
    }

    public void onClickBack(View view) {
        finish();
    }
}