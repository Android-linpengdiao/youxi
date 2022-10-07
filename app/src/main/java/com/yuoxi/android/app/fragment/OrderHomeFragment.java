package com.yuoxi.android.app.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.viewpager.widget.ViewPager;

import com.yuoxi.android.app.R;
import com.yuoxi.android.app.adapter.MainPagerAdapter;
import com.yuoxi.android.app.databinding.FragmentOrderHomeBinding;

public class OrderHomeFragment extends BaseFragment {

    private FragmentOrderHomeBinding binding;
    private int type = 0;

    public static OrderHomeFragment getInstance(int type) {
        OrderHomeFragment fragment = new OrderHomeFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("type", type);
        fragment.setArguments(bundle);
        return fragment;

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_order_home, container, false);

        if (getArguments() != null) {

            type = getArguments().getInt("type");

            MainPagerAdapter mainPagerAdapter = new MainPagerAdapter(getChildFragmentManager());
            mainPagerAdapter.addFragment("待接单", OrderHomeChildFragment.getInstance(type, 0));
            mainPagerAdapter.addFragment("已完成", OrderHomeChildFragment.getInstance(type, 1));
            mainPagerAdapter.addFragment("失效/取消", OrderHomeChildFragment.getInstance(type, 1));

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


        return binding.getRoot();
    }
}
