package com.yuoxi.android.app.adapter;

import android.content.Context;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.base.utils.CommonUtil;
import com.base.utils.GlideLoader;
import com.base.view.OnClickListener;
import com.yuoxi.android.app.R;
import com.yuoxi.android.app.databinding.ItemAjJubenBinding;
import com.yuoxi.android.app.databinding.ItemBangJubenBinding;

/**
 * 案件 - 剧本
 */
public class AJJuBenVerticalAdapter extends BaseRecyclerAdapter<String, ItemAjJubenBinding> {

    private OnClickListener onClickListener;

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public AJJuBenVerticalAdapter(Context context) {
        super(context);
    }

    @Override
    protected int getLayoutResId(int viewType) {
        return R.layout.item_aj_juben;
    }

    @Override
    protected void onBindItem(ItemAjJubenBinding binding, String dataBean, int position) {

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
        binding.tagView.setBackgroundResource(position == 0 ? R.mipmap.ic_tag_chaoji : position == 1 ? R.mipmap.ic_tag_chaoji : R.mipmap.ic_tag_chaoji);


    }
}
