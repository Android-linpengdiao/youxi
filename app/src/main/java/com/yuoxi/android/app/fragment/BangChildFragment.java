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

public class BangChildFragment extends BaseFragment {

    private FragmentBangChildBinding binding;
    private int type = 0;
    private int index = 0;

    public static BangChildFragment getInstance(int type, int index) {
        BangChildFragment fragment = new BangChildFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("type", type);
        bundle.putInt("index", index);
        fragment.setArguments(bundle);
        return fragment;

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_bang_child, container, false);

        if (getArguments() != null) {

            type = getArguments().getInt("type");
            index = getArguments().getInt("index");

            if (type == 0) {
                binding.verticalRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                BangJuBenVerticalAdapter bangJuBenVerticalAdapter = new BangJuBenVerticalAdapter(getActivity());
                binding.verticalRecyclerView.setAdapter(bangJuBenVerticalAdapter);
                bangJuBenVerticalAdapter.refreshData(CommonUtil.getTitles());

            } else if (type == 1 || type == 2) {
                binding.verticalRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                BangGaoWanVerticalAdapter bangGaoWanVerticalAdapter = new BangGaoWanVerticalAdapter(getActivity());
                binding.verticalRecyclerView.setAdapter(bangGaoWanVerticalAdapter);
                bangGaoWanVerticalAdapter.refreshData(CommonUtil.getTitles());
                binding.bangContainer.setVisibility(View.VISIBLE);

                GlideLoader.getInstance().LoaderDrawable(getActivity(), R.drawable.ic_test_cover_1, binding.bangFirstCoverView, 100);
                GlideLoader.getInstance().LoaderDrawable(getActivity(), R.drawable.ic_test_cover_1, binding.bangSecondCoverView, 100);
                GlideLoader.getInstance().LoaderDrawable(getActivity(), R.drawable.ic_test_cover_1, binding.bangThirdCoverView, 100);

            } else if (type == 3) {
                binding.verticalRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                BangGaoWanVerticalAdapter bangGaoWanVerticalAdapter = new BangGaoWanVerticalAdapter(getActivity());
                binding.verticalRecyclerView.setAdapter(bangGaoWanVerticalAdapter);
                bangGaoWanVerticalAdapter.refreshData(CommonUtil.getTitles());
                binding.bangContainer.setVisibility(View.VISIBLE);

                GlideLoader.getInstance().LoaderDrawable(getActivity(), R.drawable.ic_test_cover_1, binding.bangFirstCoverView, 100);
                GlideLoader.getInstance().LoaderDrawable(getActivity(), R.drawable.ic_test_cover_1, binding.bangSecondCoverView, 100);
                GlideLoader.getInstance().LoaderDrawable(getActivity(), R.drawable.ic_test_cover_1, binding.bangThirdCoverView, 100);

                binding.bangContainer.getLayoutParams().height = getResources().getDimensionPixelOffset(R.dimen.dp_240);
                LinearLayout.LayoutParams bangFirstLayoutParams = (LinearLayout.LayoutParams) binding.bangFirstCoverContainer.getLayoutParams();
                bangFirstLayoutParams.bottomMargin = getResources().getDimensionPixelOffset(R.dimen.dp_3);
                LinearLayout.LayoutParams bangSecondLayoutParams = (LinearLayout.LayoutParams) binding.bangSecondCoverContainer.getLayoutParams();
                bangSecondLayoutParams.bottomMargin = getResources().getDimensionPixelOffset(R.dimen.dp_3);
                LinearLayout.LayoutParams bangThirdLayoutParams = (LinearLayout.LayoutParams) binding.bangThirdCoverContainer.getLayoutParams();
                bangThirdLayoutParams.bottomMargin = getResources().getDimensionPixelOffset(R.dimen.dp_3);
            }

        }


        return binding.getRoot();
    }
}
