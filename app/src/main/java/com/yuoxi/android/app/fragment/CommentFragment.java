package com.yuoxi.android.app.fragment;

import android.graphics.Color;
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
import com.base.view.RecycleViewDivider;
import com.yuoxi.android.app.R;
import com.yuoxi.android.app.adapter.BangGaoWanVerticalAdapter;
import com.yuoxi.android.app.adapter.BangJuBenVerticalAdapter;
import com.yuoxi.android.app.adapter.CommentListAdapter;
import com.yuoxi.android.app.databinding.FragmentBangChildBinding;
import com.yuoxi.android.app.databinding.FragmentCommentBinding;

public class CommentFragment extends BaseFragment {

    private FragmentCommentBinding binding;
    private int type = 0;

    public static CommentFragment getInstance(int type) {
        CommentFragment fragment = new CommentFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("type", type);
        fragment.setArguments(bundle);
        return fragment;

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_comment, container, false);

        if (getArguments() != null) {

            type = getArguments().getInt("type");

            RecycleViewDivider divider = new RecycleViewDivider(getActivity(),
                    LinearLayoutManager.VERTICAL,
                    CommonUtil.dip2px(getActivity(), 1),
                    Color.parseColor("#0DFFFFFF"));
            binding.commentRecyclerView.addItemDecoration(divider);
            binding.commentRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            CommentListAdapter commentAdapter = new CommentListAdapter(getActivity());
            binding.commentRecyclerView.setAdapter(commentAdapter);
            commentAdapter.refreshData(CommonUtil.getTitles());

        }


        return binding.getRoot();
    }
}
