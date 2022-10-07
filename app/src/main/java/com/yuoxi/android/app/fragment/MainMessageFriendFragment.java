package com.yuoxi.android.app.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.base.Constants;
import com.base.UserInfo;
import com.base.manager.RecycleViewManager;
import com.base.view.OnClickListener;
import com.base.view.OnMultiClickListener;
import com.okhttp.ResultClient;
import com.okhttp.SendRequest;
import com.okhttp.callbacks.GenericsCallback;
import com.okhttp.sample_okhttp.JsonGenericsSerializator;
import com.quakoo.im.activity.IMChatActivity;
import com.yuoxi.android.app.MainApplication;
import com.yuoxi.android.app.R;
import com.yuoxi.android.app.activity.BlacklistActivity;
import com.yuoxi.android.app.activity.FocusFansActivity;
import com.yuoxi.android.app.activity.GroupsActivity;
import com.yuoxi.android.app.adapter.MainFriendAdapter;
import com.yuoxi.android.app.databinding.FragmentMainMessageFriendBinding;
import com.yuoxi.android.app.view.SideLetterBar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import okhttp3.Call;

public class MainMessageFriendFragment extends BaseFragment {

    private FragmentMainMessageFriendBinding binding;
    private MainFriendAdapter friendAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_main_message_friend, container, false);

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        friendAdapter = new MainFriendAdapter(getActivity());
        binding.recyclerView.setAdapter(friendAdapter);
        friendAdapter.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view, Object object) {
                Bundle bundle = new Bundle();
                switch (view.getId()) {
                    case R.id.friendContainer:
                        UserInfo userInfo = (UserInfo) object;
                        Intent intent = new Intent(getActivity(), IMChatActivity.class);
                        intent.putExtra("userInfo", userInfo); //获取聊天对象
                        intent.putExtra("chat_type", Constants.FRAGMENT_FRIEND); //获取聊天对象的类型(群聊/单聊)
                        intent.putExtra("unreadCount", 0);
                        startActivity(intent);
                        break;
                    case R.id.focusView:
                        bundle.putInt("type", 0);
                        openActivity(FocusFansActivity.class, bundle);

                        break;
                    case R.id.fansView:
                        bundle.putInt("type", 1);
                        openActivity(FocusFansActivity.class, bundle);

                        break;
                    case R.id.groupView:
                        openActivity(GroupsActivity.class);

                        break;
                    case R.id.blacklistView:
                        openActivity(BlacklistActivity.class);

                        break;
                }

            }

            @Override
            public void onLongClick(View view, Object object) {

            }
        });
        binding.sidebar.setOverlay(binding.letterOverlayTextView);
        binding.sidebar.setOnLetterChangedListener(new SideLetterBar.OnLetterChangedListener() {
            @Override
            public void onLetterChanged(String letter) {
                int position = friendAdapter.getLetterPosition(letter);
                binding.recyclerView.setScrollBarSize(position);
                RecycleViewManager.smoothMoveToPosition(binding.recyclerView, position);
            }
        });

        List<UserInfo> list = new ArrayList<>();
        list.add(new UserInfo());
        friendAdapter.refreshData(list);
        getFriendsList();

        return binding.getRoot();
    }

    private void getFriendsList() {
        SendRequest.getFriendsList(MainApplication.getUserInfo().token,
                new GenericsCallback<ResultClient<List<UserInfo>>>(new JsonGenericsSerializator()) {
                    @Override
                    public void onError(Call call, Exception e, int id) {

                    }

                    @Override
                    public void onResponse(ResultClient<List<UserInfo>> response, int id) {
                        if (response.isSuccess() && response.getData() != null) {
                            Collections.sort(response.getData(), new UserInfo());
                            friendAdapter.loadMoreData(response.getData());
                        }
                    }
                });
    }
}
