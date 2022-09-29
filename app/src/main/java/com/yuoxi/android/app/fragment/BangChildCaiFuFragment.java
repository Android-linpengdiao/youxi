package com.yuoxi.android.app.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.base.utils.CommonUtil;
import com.base.utils.GlideLoader;
import com.yuoxi.android.app.R;
import com.yuoxi.android.app.adapter.BangGaoWanVerticalAdapter;
import com.yuoxi.android.app.databinding.FragmentBangChildBinding;
import com.yuoxi.android.app.databinding.FragmentBangChildCaifuBinding;

public class BangChildCaiFuFragment extends BaseFragment {

    private FragmentBangChildCaifuBinding binding;
    private int type = 0;
    private int index = 0;

    public static BangChildCaiFuFragment getInstance(int type, int index) {
        BangChildCaiFuFragment fragment = new BangChildCaiFuFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("type", type);
        bundle.putInt("index", index);
        fragment.setArguments(bundle);
        return fragment;

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_bang_child_caifu, container, false);

        if (getArguments() != null) {

            type = getArguments().getInt("type");
            index = getArguments().getInt("index");

            binding.verticalRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            BangGaoWanVerticalAdapter bangGaoWanVerticalAdapter = new BangGaoWanVerticalAdapter(getActivity());
            binding.verticalRecyclerView.setAdapter(bangGaoWanVerticalAdapter);
            bangGaoWanVerticalAdapter.refreshData(CommonUtil.getTitles());
            binding.bangContainer.setVisibility(View.VISIBLE);

            GlideLoader.getInstance().LoaderDrawable(getActivity(), R.drawable.ic_test_cover_1, binding.bangFirstCoverView, 100);
            GlideLoader.getInstance().LoaderDrawable(getActivity(), R.drawable.ic_test_cover_1, binding.bangSecondCoverView, 100);
            GlideLoader.getInstance().LoaderDrawable(getActivity(), R.drawable.ic_test_cover_1, binding.bangThirdCoverView, 100);


        }


        return binding.getRoot();
    }
}
