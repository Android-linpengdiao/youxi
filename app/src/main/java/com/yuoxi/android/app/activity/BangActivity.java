package com.yuoxi.android.app.activity;


import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import androidx.viewpager.widget.ViewPager;

import com.yuoxi.android.app.R;
import com.yuoxi.android.app.adapter.MainPagerAdapter;
import com.yuoxi.android.app.databinding.ActivityBangBinding;
import com.yuoxi.android.app.fragment.BangFragment;

/**
 * 排行榜
 */
public class BangActivity extends BaseActivity {

    private ActivityBangBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = getViewData(R.layout.activity_bang);
        setStatusBarHeight();

        MainPagerAdapter mainPagerAdapter = new MainPagerAdapter(getSupportFragmentManager());

        mainPagerAdapter.addFragment("剧本榜", BangFragment.getInstance(0));
        mainPagerAdapter.addFragment("高玩榜", BangFragment.getInstance(1));
        mainPagerAdapter.addFragment("事务榜", BangFragment.getInstance(2));
        mainPagerAdapter.addFragment("财富榜", BangFragment.getInstance(3));

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
                if (position == 0) {
                    binding.bangBackgroundView.setBackgroundColor(Color.parseColor("#250A4F"));
                } else if (position == 1) {
                    binding.bangBackgroundView.setBackgroundResource(R.mipmap.ic_bang_gaowan_bg);
                } else if (position == 2) {
                    binding.bangBackgroundView.setBackgroundResource(R.mipmap.ic_bang_gaowan_bg);
                } else if (position == 3) {
                    binding.bangBackgroundView.setBackgroundResource(R.mipmap.ic_bang_caifu_bg);
                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    public void onClickBack(View view) {
        finish();
    }

    public void onClickRule(View view) {
        openActivity(BangRuleActivity.class);
    }
}