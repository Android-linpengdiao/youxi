package com.yuoxi.android.app.activity;

import android.os.Bundle;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.base.utils.CommonUtil;
import com.base.view.OnClickListener;
import com.okhttp.Pager;
import com.okhttp.SendRequest;
import com.okhttp.callbacks.GenericsCallback;
import com.okhttp.sample_okhttp.JsonGenericsSerializator;
import com.okhttp.utils.APIUrls;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;
import com.yuoxi.android.app.R;
import com.yuoxi.android.app.adapter.BlacklistAdapter;
import com.yuoxi.android.app.databinding.ActivityBlacklistBinding;

import okhttp3.Call;

public class BlacklistActivity extends BaseActivity {

    private ActivityBlacklistBinding binding;
    private BlacklistAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = getViewData(R.layout.activity_blacklist);

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setNestedScrollingEnabled(false);
        adapter = new BlacklistAdapter(this);
        binding.recyclerView.setAdapter(adapter);
        adapter.refreshData(CommonUtil.getTitles());
        adapter.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view, Object object) {

            }

            @Override
            public void onLongClick(View view, Object object) {

            }
        });

        setRefresh();

    }

    private Pager<String> pager = new Pager<>();
    private void setRefresh() {
        binding.refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                pager = new Pager<>();
//                loadData(true);
            }
        });
        binding.refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshlayout) {
//                loadData(false);

            }
        });
        binding.refreshLayout.autoRefresh();
    }

    private void loadData(boolean isRefresh) {
        String url = APIUrls.getFansPager;
        SendRequest.getFocusFansPager(getUserInfo().getToken(),0, 1, pager.getNextCursor(), url,
                new GenericsCallback<Pager<String>>(new JsonGenericsSerializator()) {

                    @Override
                    public void onAfter(int id) {
                        super.onAfter(id);
                        if (isRefresh) {
                            binding.refreshLayout.finishRefresh();
                        } else {
                            binding.refreshLayout.finishLoadMore();
                        }
                    }

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        if (isRefresh) {
                            binding.refreshLayout.finishRefresh(false);
                        } else {
                            binding.refreshLayout.finishLoadMore(false);
                        }
                    }

                    @Override
                    public void onResponse(Pager<String> response, int id) {
                        pager = response;
                        if (response != null && response.getData() != null) {
                            if (isRefresh) {
                                adapter.refreshData(response.getData());
                            } else {
                                adapter.loadMoreData(response.getData());
                            }
                            if (!response.isHasnext()) {
                                binding.refreshLayout.setNoMoreData(true);
                            }
                            binding.emptyView.setVisibility(adapter.getList().size() > 0 ? View.GONE : View.VISIBLE);
                        }
                    }
                });
    }

    public void onClickBack(View view) {
        finish();
    }
}