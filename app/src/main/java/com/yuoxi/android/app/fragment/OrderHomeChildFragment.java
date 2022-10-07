package com.yuoxi.android.app.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.base.utils.CommonUtil;
import com.base.view.OnClickListener;
import com.yuoxi.android.app.R;
import com.yuoxi.android.app.adapter.BlacklistAdapter;
import com.yuoxi.android.app.adapter.OrderAdapter;
import com.yuoxi.android.app.databinding.FragmentOrderHomeChildBinding;

public class OrderHomeChildFragment extends BaseFragment {

    private FragmentOrderHomeChildBinding binding;
    private int type = 0;
    private int index = 0;
    private OrderAdapter adapter;

    public static OrderHomeChildFragment getInstance(int type, int index) {
        OrderHomeChildFragment fragment = new OrderHomeChildFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("type", type);
        bundle.putInt("index", index);
        fragment.setArguments(bundle);
        return fragment;

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_order_home_child, container, false);

        if (getArguments() != null) {

            type = getArguments().getInt("type");
            index = getArguments().getInt("index");

            binding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            adapter = new OrderAdapter(getActivity());
            binding.recyclerView.setAdapter(adapter);
            adapter.refreshData(CommonUtil.getTitles());
            adapter.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view, Object object) {

                }

                @Override
                public void onLongClick(View view, Object object) {

                }
            });

        }


        return binding.getRoot();
    }
}
