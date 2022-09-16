package com.yuoxi.android.app.activity;

import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.view.View;

import com.base.view.OnClickListener;
import com.okhttp.Pager;
import com.okhttp.SendRequest;
import com.okhttp.callbacks.GenericsCallback;
import com.okhttp.sample_okhttp.JsonGenericsSerializator;
import com.okhttp.utils.APIUrls;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;
import com.yuoxi.android.app.Callback;
import com.yuoxi.android.app.R;
import com.yuoxi.android.app.adapter.FocusFansAdapter;
import com.yuoxi.android.app.databinding.ActivityFocusFansBinding;
import com.yuoxi.android.app.model.FocusFansBean;

import okhttp3.Call;

public class FocusFansActivity extends BaseActivity {

    private ActivityFocusFansBinding binding;
    private FocusFansAdapter focusFansAdapter;
    private Pager<FocusFansBean> pager = new Pager<>();
    private int uid = 0;
    private int type = 0;//0-关注   1-粉丝

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = getViewData(R.layout.activity_focus_fans);

        uid = getIntent().getIntExtra("uid", 0);
        type = getIntent().getIntExtra("type", 0);
        if (type == 0) {
            binding.titleView.setText("关注");
        } else {
            binding.titleView.setText("粉丝");
        }

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(FocusFansActivity.this));
        binding.recyclerView.setNestedScrollingEnabled(false);
        focusFansAdapter = new FocusFansAdapter(FocusFansActivity.this);
        focusFansAdapter.setType(type);
        binding.recyclerView.setAdapter(focusFansAdapter);
        focusFansAdapter.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view, Object object) {
                FocusFansBean dataBean = (FocusFansBean) object;
                int uid;
                if (type == 0) {
                    uid = dataBean.getTypeUser().getId();
                } else {
                    uid = dataBean.getUser().getId();
                }
                switch (view.getId()) {
                    case R.id.focusView:
                        String url = dataBean.getFocus() == 1 ? APIUrls.focusfansDel : APIUrls.focusfansAdd;
                        focusFans(1, uid, url, new Callback() {
                            @Override
                            public void onError() {

                            }

                            @Override
                            public void onResponse(boolean success,int id) {
                                if (success) {
                                    dataBean.setFocus(dataBean.getFocus() == 1 ? 0 : 1);
                                    focusFansAdapter.notifyDataSetChanged();
                                }
                            }
                        });
                        break;
                    case R.id.view_layout:
                        Bundle bundle = new Bundle();
                        openActivity(UserInfoActivity.class, bundle);
                        break;
                }
            }

            @Override
            public void onLongClick(View view, Object object) {

            }
        });

        setRefresh();

    }

    private void setRefresh() {
        binding.refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                pager = new Pager<>();
                loadData(true);
            }
        });
        binding.refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshlayout) {
                loadData(false);

            }
        });
        binding.refreshLayout.autoRefresh();
    }

    private void loadData(boolean isRefresh) {
        String url = type == 0 ? APIUrls.getFocusPager : APIUrls.getFansPager;
        SendRequest.getFocusFansPager(getUserInfo().getToken(),uid, 1, pager.getNextCursor(), url,
                new GenericsCallback<Pager<FocusFansBean>>(new JsonGenericsSerializator()) {

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
                    public void onResponse(Pager<FocusFansBean> response, int id) {
                        pager = response;
                        if (response != null && response.getData() != null) {
                            if (isRefresh) {
                                focusFansAdapter.refreshData(response.getData());
                            } else {
                                focusFansAdapter.loadMoreData(response.getData());
                            }
                            if (!response.isHasnext()) {
                                binding.refreshLayout.setNoMoreData(true);
                            }
                            binding.emptyView.setVisibility(focusFansAdapter.getList().size() > 0 ? View.GONE : View.VISIBLE);
                        }
                    }
                });
    }

    public void onClickBack(View view) {
        finish();
    }
}