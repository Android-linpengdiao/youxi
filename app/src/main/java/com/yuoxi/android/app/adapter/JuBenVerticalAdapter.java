package com.yuoxi.android.app.adapter;

import android.content.Context;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.base.utils.CommonUtil;
import com.base.utils.GlideLoader;
import com.base.view.OnClickListener;
import com.base.view.OnMultiClickListener;
import com.yuoxi.android.app.R;
import com.yuoxi.android.app.databinding.ItemJubenBinding;

/**
 * 剧本杀 - 剧本
 */
public class JuBenVerticalAdapter extends BaseRecyclerAdapter<String, ItemJubenBinding> {

    private OnClickListener onClickListener;

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public JuBenVerticalAdapter(Context context) {
        super(context);
    }

    @Override
    protected int getLayoutResId(int viewType) {
        return R.layout.item_juben;
    }

    @Override
    protected void onBindItem(ItemJubenBinding binding, String dataBean, int position) {

        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        binding.tagRecyclerView.setLayoutManager(layoutManager);
        HomeLinearTagAdapter tagAdapter = new HomeLinearTagAdapter(mContext);
        binding.tagRecyclerView.setAdapter(tagAdapter);
        tagAdapter.refreshData(CommonUtil.getTitles());

        LinearLayoutManager userLayoutManager = new LinearLayoutManager(mContext);
        userLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        binding.userRecyclerView.setLayoutManager(userLayoutManager);
        UserIconAdapter userAdapter = new UserIconAdapter(mContext);
        binding.userRecyclerView.setAdapter(userAdapter);
        userAdapter.refreshData(CommonUtil.getTitles());

//        binding.titleView.setText(null);
        GlideLoader.getInstance().LoaderDrawable(mContext, R.drawable.ic_test_cover, binding.coverView);
        binding.tagView.setVisibility(position < 3 ? View.VISIBLE : View.GONE);
        binding.getRoot().setBackgroundResource(position == 0 ? R.mipmap.ic_bang_juben_2 : R.drawable.item_bg);

        binding.coverView.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void OnMultiClick(View view) {
                if (onClickListener != null) {
                    onClickListener.onClick(view, dataBean);
                }
            }
        });
        binding.getRoot().setOnClickListener(new OnMultiClickListener() {
            @Override
            public void OnMultiClick(View view) {
                if (onClickListener != null) {
                    onClickListener.onClick(view, dataBean);
                }
            }
        });

    }
}
