package com.yuoxi.android.app.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.base.utils.CommonUtil;
import com.base.view.RecycleViewDivider;
import com.yuoxi.android.app.R;
import com.yuoxi.android.app.adapter.HomeLinearAdapter;
import com.yuoxi.android.app.databinding.FragmentMainHomeBinding;

public class MainHomeFragment extends BaseFragment {

    private FragmentMainHomeBinding binding;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_main_home, container, false);

        RecycleViewDivider divider = new RecycleViewDivider(getActivity(),
                LinearLayoutManager.VERTICAL,
                CommonUtil.dip2px(getActivity(), 10),
                Color.parseColor("#181526"));

        binding.recyclerView.addItemDecoration(divider);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        HomeLinearAdapter homeLinearAdapter = new HomeLinearAdapter(getActivity());
        binding.recyclerView.setAdapter(homeLinearAdapter);
        homeLinearAdapter.refreshData(CommonUtil.getTitles());

        return binding.getRoot();
    }
}
