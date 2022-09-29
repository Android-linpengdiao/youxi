package com.yuoxi.android.app.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.base.utils.CommonUtil;
import com.base.utils.GlideLoader;
import com.base.view.OnClickListener;
import com.base.view.RecycleViewDivider;
import com.yuoxi.android.app.R;
import com.yuoxi.android.app.databinding.ItemBangJubenBinding;
import com.yuoxi.android.app.databinding.ItemHomeLinearVerticalBinding;

/**
 * 榜单 - 剧本
 */
public class BangJuBenVerticalAdapter extends BaseRecyclerAdapter<String, ItemBangJubenBinding> {

    private OnClickListener onClickListener;

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public BangJuBenVerticalAdapter(Context context) {
        super(context);
    }

    @Override
    protected int getLayoutResId(int viewType) {
        return R.layout.item_bang_juben;
    }

    @Override
    protected void onBindItem(ItemBangJubenBinding binding, String dataBean, int position) {

        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        binding.tagRecyclerView.setLayoutManager(layoutManager);
        HomeLinearTagAdapter tagAdapter = new HomeLinearTagAdapter(mContext);
        binding.tagRecyclerView.setAdapter(tagAdapter);
        tagAdapter.refreshData(CommonUtil.getTitles());

//        binding.titleView.setText(null);
        GlideLoader.getInstance().LoaderDrawable(mContext, R.drawable.ic_test_cover, binding.coverView);
        binding.tagView.setVisibility(position < 3 ? View.VISIBLE : View.GONE);
        binding.tagView.setText(String.valueOf(position + 1));
        binding.tagView.setBackgroundResource(position == 0 ? R.mipmap.ic_bang_juben_tag_1 : position == 1 ? R.mipmap.ic_bang_juben_tag_2 : R.mipmap.ic_bang_juben_tag_3);
        binding.getRoot().setBackgroundResource(position == 0 ? R.mipmap.ic_bang_juben_1 : position == 1 ? R.mipmap.ic_bang_juben_2 : R.mipmap.ic_bang_juben_3);


    }
}
