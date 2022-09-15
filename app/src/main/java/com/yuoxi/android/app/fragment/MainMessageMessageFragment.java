package com.yuoxi.android.app.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.base.utils.CommonUtil;
import com.yuoxi.android.app.R;
import com.yuoxi.android.app.adapter.HomeLinearVerticalAdapter;
import com.yuoxi.android.app.adapter.MainMessageAdapter;
import com.yuoxi.android.app.adapter.MainPagerAdapter;
import com.yuoxi.android.app.databinding.FragmentMainMessageBinding;
import com.yuoxi.android.app.databinding.FragmentMainMessageMessageBinding;

public class MainMessageMessageFragment extends BaseFragment {

    private FragmentMainMessageMessageBinding binding;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_main_message_message, container, false);

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        MainMessageAdapter messageAdapter = new MainMessageAdapter(getActivity());
        binding.recyclerView.setAdapter(messageAdapter);
        messageAdapter.refreshData(CommonUtil.getTitles());

        return binding.getRoot();
    }
}
