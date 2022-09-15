package com.yuoxi.android.app.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.base.UserInfo;
import com.base.manager.RecycleViewManager;
import com.base.utils.CommonUtil;
import com.yuoxi.android.app.R;
import com.yuoxi.android.app.adapter.MainFriendAdapter;
import com.yuoxi.android.app.adapter.MainMessageAdapter;
import com.yuoxi.android.app.databinding.FragmentMainMessageFriendBinding;
import com.yuoxi.android.app.view.SideLetterBar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainMessageFriendFragment extends BaseFragment {

    private FragmentMainMessageFriendBinding binding;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_main_message_friend, container, false);

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        MainFriendAdapter messageAdapter = new MainFriendAdapter(getActivity());
        binding.recyclerView.setAdapter(messageAdapter);

        binding.sidebar.setOverlay(binding.letterOverlayTextView);
        binding.sidebar.setOnLetterChangedListener(new SideLetterBar.OnLetterChangedListener() {
            @Override
            public void onLetterChanged(String letter) {
                int position = messageAdapter.getLetterPosition(letter);
                binding.recyclerView.setScrollBarSize(position);
                RecycleViewManager.smoothMoveToPosition(binding.recyclerView, position);
            }
        });

        List<UserInfo> userInfoList = new ArrayList<>();

        userInfoList.add(new UserInfo("玛卡巴卡"));
        userInfoList.add(new UserInfo("桐桐"));
        userInfoList.add(new UserInfo("明明"));
        userInfoList.add(new UserInfo("庆庆"));
        userInfoList.add(new UserInfo("丽丽"));
        Collections.sort(userInfoList, new UserInfo());
        messageAdapter.refreshData(userInfoList);

        return binding.getRoot();
    }
}
