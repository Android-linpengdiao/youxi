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
import com.yuoxi.android.app.adapter.BangGaoWanVerticalAdapter;
import com.yuoxi.android.app.adapter.BangJuBenVerticalAdapter;
import com.yuoxi.android.app.databinding.FragmentBangChildBinding;
import com.yuoxi.android.app.databinding.FragmentBangRuleChildBinding;

public class BangRuleChildFragment extends BaseFragment {

    private FragmentBangRuleChildBinding binding;
    private int type = 0;
    private int index = 0;

    public static BangRuleChildFragment getInstance(int type, int index) {
        BangRuleChildFragment fragment = new BangRuleChildFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("type", type);
        bundle.putInt("index", index);
        fragment.setArguments(bundle);
        return fragment;

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_bang_rule_child, container, false);

        if (getArguments() != null) {

            type = getArguments().getInt("type");
            index = getArguments().getInt("index");

        }


        return binding.getRoot();
    }
}
