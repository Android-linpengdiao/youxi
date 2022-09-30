package com.yuoxi.android.app.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.viewpager.widget.ViewPager;

import com.base.utils.CommonUtil;
import com.yuoxi.android.app.R;
import com.yuoxi.android.app.activity.shiwu.SWGoldActivity;
import com.yuoxi.android.app.adapter.BangJuBenVerticalAdapter;
import com.yuoxi.android.app.adapter.MainPagerAdapter;
import com.yuoxi.android.app.adapter.SWGoldAdapter;
import com.yuoxi.android.app.adapter.SWTaskAdapter;
import com.yuoxi.android.app.databinding.FragmentBangBinding;
import com.yuoxi.android.app.databinding.FragmentSwHomeBinding;

public class SWHomeFragment extends BaseFragment {

    private FragmentSwHomeBinding binding;
    private int type = 0;

    public static SWHomeFragment getInstance(int type) {
        SWHomeFragment fragment = new SWHomeFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("type", type);
        fragment.setArguments(bundle);
        return fragment;

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_sw_home, container, false);

        if (getArguments() != null) {

            type = getArguments().getInt("type");

            binding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            binding.recyclerView.setNestedScrollingEnabled(false);
            SWTaskAdapter adapter = new SWTaskAdapter(getActivity());
            binding.recyclerView.setAdapter(adapter);
            adapter.refreshData(CommonUtil.getTitles());

        }


        return binding.getRoot();
    }
}
