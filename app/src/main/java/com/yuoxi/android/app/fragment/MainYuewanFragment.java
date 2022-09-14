package com.yuoxi.android.app.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.base.utils.CommonUtil;
import com.base.view.GridItemDecoration;
import com.base.view.RecycleViewDivider;
import com.youth.banner.BannerConfig;
import com.youth.banner.listener.OnBannerListener;
import com.yuoxi.android.app.R;
import com.yuoxi.android.app.adapter.HomeLinearHorizontalAdapter;
import com.yuoxi.android.app.adapter.HomeLinearVerticalAdapter;
import com.yuoxi.android.app.adapter.YuewanGridAdapter;
import com.yuoxi.android.app.adapter.YuewanLinearVerticalAdapter;
import com.yuoxi.android.app.databinding.FragmentMainHomeBinding;
import com.yuoxi.android.app.databinding.FragmentMainYuewanBinding;
import com.yuoxi.android.app.loader.GlideImageLoader;

import java.util.ArrayList;
import java.util.List;

public class MainYuewanFragment extends BaseFragment {

    private FragmentMainYuewanBinding binding;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_main_yuewan, container, false);
        setStatusBarHeight(binding.getRoot());

        initBanner();

        GridItemDecoration.Builder builder = new GridItemDecoration.Builder(getActivity());
        builder.color(R.color.transparent);
        builder.size(CommonUtil.dip2px(getActivity(), 10));
        binding.gridRecyclerView.addItemDecoration(new GridItemDecoration(builder));
        binding.gridRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(),4));
        YuewanGridAdapter gridAdapter = new YuewanGridAdapter(getActivity());
        binding.gridRecyclerView.setAdapter(gridAdapter);
        gridAdapter.refreshData(CommonUtil.getTitles().subList(0,8));

        RecycleViewDivider verticalDivider = new RecycleViewDivider(getActivity(),
                LinearLayoutManager.VERTICAL,
                CommonUtil.dip2px(getActivity(), 10),
                Color.parseColor("#181526"));
        binding.verticalRecyclerView.addItemDecoration(verticalDivider);
        binding.verticalRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        YuewanLinearVerticalAdapter yuewanLinearVerticalAdapter = new YuewanLinearVerticalAdapter(getActivity());
        binding.verticalRecyclerView.setAdapter(yuewanLinearVerticalAdapter);
        yuewanLinearVerticalAdapter.refreshData(CommonUtil.getTitles());

        return binding.getRoot();
    }

    private void initBanner() {
        List<String> titles = new ArrayList<>();
        List<String> imageUrls = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            titles.add("");
            imageUrls.add("");
        }
        binding.banner.setVisibility(View.VISIBLE);
        binding.banner.setImages(imageUrls)
                .setBannerTitles(titles)
                .setImageLoader(new GlideImageLoader())
                .setBannerStyle(BannerConfig.CIRCLE_INDICATOR)
                .setDelayTime(5000)
                .start();
        binding.banner.setOnBannerListener(new OnBannerListener() {
            @Override
            public void OnBannerClick(int position) {

            }
        });
    }
}
