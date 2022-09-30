package com.yuoxi.android.app.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.base.utils.CommonUtil;
import com.base.utils.GlideLoader;
import com.yuoxi.android.app.R;
import com.yuoxi.android.app.adapter.AJJuBenVerticalAdapter;
import com.yuoxi.android.app.adapter.BangGaoWanVerticalAdapter;
import com.yuoxi.android.app.adapter.BangJuBenVerticalAdapter;
import com.yuoxi.android.app.databinding.FragmentBangChildBinding;

public class AJHomeFragment extends BaseFragment {

    private FragmentBangChildBinding binding;
    private int type = 0;

    public static AJHomeFragment getInstance(int type) {
        AJHomeFragment fragment = new AJHomeFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("type", type);
        fragment.setArguments(bundle);
        return fragment;

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_bang_child, container, false);

        if (getArguments() != null) {

            type = getArguments().getInt("type");

            binding.verticalRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            AJJuBenVerticalAdapter juBenVerticalAdapter = new AJJuBenVerticalAdapter(getActivity());
            binding.verticalRecyclerView.setAdapter(juBenVerticalAdapter);
            juBenVerticalAdapter.refreshData(CommonUtil.getTitles());

        }


        return binding.getRoot();
    }
}
