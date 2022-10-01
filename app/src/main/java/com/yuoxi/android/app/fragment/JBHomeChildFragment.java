package com.yuoxi.android.app.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.base.manager.DialogManager;
import com.base.utils.CommonUtil;
import com.base.utils.GlideLoader;
import com.base.view.OnClickListener;
import com.yuoxi.android.app.R;
import com.yuoxi.android.app.activity.SearchActivity;
import com.yuoxi.android.app.activity.juben.JuBenDetailsActivity;
import com.yuoxi.android.app.adapter.BangGaoWanVerticalAdapter;
import com.yuoxi.android.app.adapter.BangJuBenVerticalAdapter;
import com.yuoxi.android.app.adapter.JuBenVerticalAdapter;
import com.yuoxi.android.app.databinding.FragmentBangChildBinding;
import com.yuoxi.android.app.databinding.FragmentJbHomeChildBinding;
import com.yuoxi.android.app.db.DBManager;

public class JBHomeChildFragment extends BaseFragment {

    private FragmentJbHomeChildBinding binding;
    private int type = 0;
    private int index = 0;

    public static JBHomeChildFragment getInstance(int type, int index) {
        JBHomeChildFragment fragment = new JBHomeChildFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("type", type);
        bundle.putInt("index", index);
        fragment.setArguments(bundle);
        return fragment;

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_jb_home_child, container, false);

        if (getArguments() != null) {

            type = getArguments().getInt("type");
            index = getArguments().getInt("index");

            binding.verticalRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            JuBenVerticalAdapter juBenVerticalAdapter = new JuBenVerticalAdapter(getActivity());
            binding.verticalRecyclerView.setAdapter(juBenVerticalAdapter);
            juBenVerticalAdapter.refreshData(CommonUtil.getTitles());
            juBenVerticalAdapter.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view, Object object) {
                    switch (view.getId()) {
                        case R.id.coverView:
                            openActivity(JuBenDetailsActivity.class);
                            break;
                        default:
                            DialogManager.getInstance().confirmDialog(getActivity(),
                                    "案件已开始", "当前案件已开始，是否参与围观", "取消", "去围观",
                                    new DialogManager.Listener() {
                                        @Override
                                        public void onItemLeft() {

                                        }

                                        @Override
                                        public void onItemRight() {
                                        }
                                    });
                            break;
                    }

                }

                @Override
                public void onLongClick(View view, Object object) {

                }
            });

        }


        return binding.getRoot();
    }
}
