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
import com.yuoxi.android.app.databinding.FragmentBangRuleBinding;

public class BangRuleFragment extends BaseFragment {

    private FragmentBangRuleBinding binding;
    private int type = 0;

    public static BangRuleFragment getInstance(int type) {
        BangRuleFragment fragment = new BangRuleFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("type", type);
        fragment.setArguments(bundle);
        return fragment;

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_bang_rule, container, false);

        if (getArguments() != null) {

            type = getArguments().getInt("type");

            MainPagerAdapter mainPagerAdapter = new MainPagerAdapter(getChildFragmentManager());
            mainPagerAdapter.addFragment("月度榜", BangRuleChildFragment.getInstance(type, 0));
            mainPagerAdapter.addFragment("总榜", BangRuleChildFragment.getInstance(type, 1));

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
