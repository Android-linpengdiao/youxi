package com.yuoxi.android.app.activity.anjian;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.viewpager.widget.ViewPager;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import com.base.utils.CommonUtil;
import com.yuoxi.android.app.R;
import com.yuoxi.android.app.activity.BaseActivity;
import com.yuoxi.android.app.activity.SearchActivity;
import com.yuoxi.android.app.adapter.FilterTypeAdapter;
import com.yuoxi.android.app.adapter.HomeLinearTagAdapter;
import com.yuoxi.android.app.adapter.MainPagerAdapter;
import com.yuoxi.android.app.databinding.ActivityAJHomeBinding;
import com.yuoxi.android.app.databinding.ActivityBangBinding;
import com.yuoxi.android.app.fragment.AJHomeFragment;
import com.yuoxi.android.app.fragment.BangFragment;

public class AJHomeActivity extends BaseActivity {

    private ActivityAJHomeBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = getViewData(R.layout.activity_a_j_home);
        setStatusBarHeight();

        MainPagerAdapter mainPagerAdapter = new MainPagerAdapter(getSupportFragmentManager());

        mainPagerAdapter.addFragment("案件室", AJHomeFragment.getInstance(0));
        mainPagerAdapter.addFragment("已解锁", AJHomeFragment.getInstance(1));

        binding.viewPager.setAdapter(mainPagerAdapter);
        binding.viewPager.setCurrentItem(0);
        binding.viewPager.setOffscreenPageLimit(mainPagerAdapter.getCount());
        binding.tabLayout.setupWithViewPager(binding.viewPager);
        binding.viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {


            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        initFilter();

    }

    private void initFilter() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(AJHomeActivity.this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        binding.sortRecyclerView.setLayoutManager(layoutManager);
        FilterTypeAdapter filterTypeAdapter = new FilterTypeAdapter(AJHomeActivity.this);
        binding.sortRecyclerView.setAdapter(filterTypeAdapter);
        filterTypeAdapter.refreshData(CommonUtil.getTitles());

        LinearLayoutManager reshuLayoutManager = new LinearLayoutManager(AJHomeActivity.this);
        reshuLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        binding.peopleRecyclerView.setLayoutManager(reshuLayoutManager);
        FilterTypeAdapter renshuAdapter = new FilterTypeAdapter(AJHomeActivity.this);
        binding.peopleRecyclerView.setAdapter(renshuAdapter);
        renshuAdapter.refreshData(CommonUtil.getTitles());

        LinearLayoutManager ticaiLayoutManager = new LinearLayoutManager(AJHomeActivity.this);
        ticaiLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        binding.ticaiRecyclerView.setLayoutManager(ticaiLayoutManager);
        FilterTypeAdapter ticaiAdapter = new FilterTypeAdapter(AJHomeActivity.this);
        binding.ticaiRecyclerView.setAdapter(ticaiAdapter);
        ticaiAdapter.refreshData(CommonUtil.getTitles());

        LinearLayoutManager shidaiLayoutManager = new LinearLayoutManager(AJHomeActivity.this);
        shidaiLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        binding.shidaiRecyclerView.setLayoutManager(shidaiLayoutManager);
        FilterTypeAdapter shidaiAdapter = new FilterTypeAdapter(AJHomeActivity.this);
        binding.shidaiRecyclerView.setAdapter(shidaiAdapter);
        shidaiAdapter.refreshData(CommonUtil.getTitles());

        LinearLayoutManager nanduLayoutManager = new LinearLayoutManager(AJHomeActivity.this);
        nanduLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        binding.nanduRecyclerView.setLayoutManager(nanduLayoutManager);
        FilterTypeAdapter nanduAdapter = new FilterTypeAdapter(AJHomeActivity.this);
        binding.nanduRecyclerView.setAdapter(nanduAdapter);
        nanduAdapter.refreshData(CommonUtil.getTitles());

        LinearLayoutManager jiageLayoutManager = new LinearLayoutManager(AJHomeActivity.this);
        jiageLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        binding.jiageRecyclerView.setLayoutManager(jiageLayoutManager);
        FilterTypeAdapter jiageAdapter = new FilterTypeAdapter(AJHomeActivity.this);
        binding.jiageRecyclerView.setAdapter(jiageAdapter);
        jiageAdapter.refreshData(CommonUtil.getTitles());

        LinearLayoutManager youwanLayoutManager = new LinearLayoutManager(AJHomeActivity.this);
        youwanLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        binding.youwanRecyclerView.setLayoutManager(youwanLayoutManager);
        FilterTypeAdapter youwanAdapter = new FilterTypeAdapter(AJHomeActivity.this);
        binding.youwanRecyclerView.setAdapter(youwanAdapter);
        youwanAdapter.refreshData(CommonUtil.getTitles());
    }

    public void onClickFilter(View view) {
        binding.filterContainer.setVisibility(binding.filterContainer.isShown() ? View.GONE : View.VISIBLE);
    }

    public void onClickBack(View view) {
        finish();
    }

    public void onClickSearch(View view) {
        openActivity(SearchActivity.class);
    }
}