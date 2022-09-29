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
import com.yuoxi.android.app.databinding.FragmentBangBinding;

public class BangFragment extends BaseFragment {

    private FragmentBangBinding binding;
    private int type = 0;

    public static BangFragment getInstance(int type) {
        BangFragment fragment = new BangFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("type", type);
        fragment.setArguments(bundle);
        return fragment;

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_bang, container, false);

        if (getArguments() != null) {

            type = getArguments().getInt("type");

            MainPagerAdapter mainPagerAdapter = new MainPagerAdapter(getChildFragmentManager());

            if (type == 0) {
                mainPagerAdapter.addFragment("畅销榜", BangChildFragment.getInstance(type, 0));
                mainPagerAdapter.addFragment("评分榜", BangChildFragment.getInstance(type, 1));
            } else if (type == 1) {
                mainPagerAdapter.addFragment("月榜", BangChildFragment.getInstance(type, 0));
                mainPagerAdapter.addFragment("总榜", BangChildFragment.getInstance(type, 1));
            } else if (type == 2) {
                mainPagerAdapter.addFragment("接单榜", BangChildFragment.getInstance(type, 0));
                mainPagerAdapter.addFragment("委托榜", BangChildFragment.getInstance(type, 1));
            } else if (type == 3) {
                mainPagerAdapter.addFragment("月度榜", BangChildCaiFuFragment.getInstance(type, 0));
                mainPagerAdapter.addFragment("总榜", BangChildCaiFuFragment.getInstance(type, 1));
            }

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
