package com.yuoxi.android.app.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.base.utils.CommonUtil;
import com.base.view.RecycleViewDivider;
import com.okhttp.ResultClient;
import com.okhttp.SendRequest;
import com.okhttp.callbacks.GenericsCallback;
import com.okhttp.sample_okhttp.JsonGenericsSerializator;
import com.okhttp.utils.APIUrls;
import com.okhttp.utils.OkHttpUtils;
import com.youth.banner.BannerConfig;
import com.youth.banner.listener.OnBannerListener;
import com.yuoxi.android.app.R;
import com.yuoxi.android.app.adapter.HomeLinearHorizontalAdapter;
import com.yuoxi.android.app.adapter.HomeLinearVerticalAdapter;
import com.yuoxi.android.app.databinding.FragmentMainHomeBinding;
import com.yuoxi.android.app.loader.GlideImageLoader;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;

public class MainHomeFragment extends BaseFragment {

    private FragmentMainHomeBinding binding;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_main_home, container, false);

        initBanner();

        RecycleViewDivider horizontalDivider = new RecycleViewDivider(getActivity(),
                LinearLayoutManager.HORIZONTAL,
                CommonUtil.dip2px(getActivity(), 10),
                Color.parseColor("#181526"));
        binding.horizontalRecyclerView.addItemDecoration(horizontalDivider);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        binding.horizontalRecyclerView.setLayoutManager(layoutManager);
        HomeLinearHorizontalAdapter homeLinearHorizontalAdapter = new HomeLinearHorizontalAdapter(getActivity());
        binding.horizontalRecyclerView.setAdapter(homeLinearHorizontalAdapter);
        homeLinearHorizontalAdapter.refreshData(CommonUtil.getTitles());

        RecycleViewDivider verticalDivider = new RecycleViewDivider(getActivity(),
                LinearLayoutManager.VERTICAL,
                CommonUtil.dip2px(getActivity(), 10),
                Color.parseColor("#181526"));
        binding.verticalRecyclerView.addItemDecoration(verticalDivider);
        binding.verticalRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        HomeLinearVerticalAdapter homeLinearVerticalAdapter = new HomeLinearVerticalAdapter(getActivity());
        binding.verticalRecyclerView.setAdapter(homeLinearVerticalAdapter);
        homeLinearVerticalAdapter.refreshData(CommonUtil.getTitles());

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
