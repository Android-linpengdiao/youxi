package com.yuoxi.android.app.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.yuoxi.android.app.R;
import com.yuoxi.android.app.adapter.MainPagerAdapter;
import com.yuoxi.android.app.databinding.FragmentMainHomeBinding;
import com.yuoxi.android.app.databinding.FragmentMainMessageBinding;

public class MainMessageFragment extends BaseFragment {

    private FragmentMainMessageBinding binding;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_main_message, container, false);
        setStatusBarHeight(binding.getRoot());

        MainPagerAdapter mainPagerAdapter = new MainPagerAdapter(getChildFragmentManager());
        mainPagerAdapter.addFragment("消息", new MainMessageMessageFragment());
        mainPagerAdapter.addFragment("好友", new MainMessageFriendFragment());

        binding.viewPager.setAdapter(mainPagerAdapter);
        binding.viewPager.setOffscreenPageLimit(mainPagerAdapter.getCount());
        binding.viewPager.setCurrentItem(0);
        binding.tabLayout.setupWithViewPager(binding.viewPager);

        return binding.getRoot();
    }
}
