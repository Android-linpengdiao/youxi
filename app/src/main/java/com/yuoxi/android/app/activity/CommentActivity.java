package com.yuoxi.android.app.activity;

import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import com.yuoxi.android.app.R;
import com.yuoxi.android.app.adapter.MainPagerAdapter;
import com.yuoxi.android.app.databinding.ActivityCommentBinding;
import com.yuoxi.android.app.fragment.CommentFragment;

public class CommentActivity extends BaseActivity {

    private ActivityCommentBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = getViewData(R.layout.activity_comment);

        MainPagerAdapter mainPagerAdapter = new MainPagerAdapter(getSupportFragmentManager());

        mainPagerAdapter.addFragment("全部", CommentFragment.getInstance(0));
        mainPagerAdapter.addFragment("最新", CommentFragment.getInstance(1));
        mainPagerAdapter.addFragment("好评", CommentFragment.getInstance(1));
        mainPagerAdapter.addFragment("差评", CommentFragment.getInstance(1));

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