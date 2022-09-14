package com.yuoxi.android.app.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.base.utils.GlideLoader;
import com.yuoxi.android.app.R;
import com.yuoxi.android.app.databinding.FragmentMainHomeBinding;
import com.yuoxi.android.app.databinding.FragmentMainMineBinding;

public class MainMineFragment extends BaseFragment {

    private FragmentMainMineBinding binding;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_main_mine, container, false);
        setStatusBarHeight(binding.getRoot());

        GlideLoader.LoaderDrawable(getActivity(), R.drawable.ic_test_user_icon, binding.userIconView);

        return binding.getRoot();
    }
}
