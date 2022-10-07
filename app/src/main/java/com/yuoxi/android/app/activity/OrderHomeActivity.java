package com.yuoxi.android.app.activity;


import android.net.Uri;
import android.os.Bundle;

import androidx.viewpager.widget.ViewPager;

import com.yuoxi.android.app.R;
import com.yuoxi.android.app.adapter.MainPagerAdapter;
import com.yuoxi.android.app.databinding.ActivityOrderHomeBinding;
import com.yuoxi.android.app.fragment.BangRuleFragment;
import com.yuoxi.android.app.fragment.OrderHomeFragment;

public class OrderHomeActivity extends BaseActivity {

    private ActivityOrderHomeBinding binding;
    private int uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = getViewData(R.layout.activity_order_home);

        uid = getIntent().getIntExtra("uid", 0);
        Uri uri = getIntent().getData();
        if (uri != null) {
            String host = uri.getHost();
            String scheme = uri.getScheme();
            uid = Integer.parseInt(uri.getQueryParameter("uid"));
        }

        MainPagerAdapter mainPagerAdapter = new MainPagerAdapter(getSupportFragmentManager());

        mainPagerAdapter.addFragment("粉丝", OrderHomeFragment.getInstance(0));
        mainPagerAdapter.addFragment("约玩订单", OrderHomeFragment.getInstance(1));

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
}