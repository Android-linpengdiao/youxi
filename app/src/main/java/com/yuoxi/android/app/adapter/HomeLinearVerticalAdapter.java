package com.yuoxi.android.app.adapter;

import android.content.Context;
import android.graphics.Color;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.base.utils.CommonUtil;
import com.base.view.OnClickListener;
import com.base.view.RecycleViewDivider;
import com.yuoxi.android.app.R;
import com.yuoxi.android.app.databinding.ItemHomeLinearVerticalBinding;

/**
 * 首页 - 竖布局
 */
public class HomeLinearVerticalAdapter extends BaseRecyclerAdapter<String, ItemHomeLinearVerticalBinding> {

    private OnClickListener onClickListener;

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public HomeLinearVerticalAdapter(Context context) {
        super(context);
    }

    @Override
    protected int getLayoutResId(int viewType) {
        return R.layout.item_home_linear_vertical;
    }

    @Override
    protected void onBindItem(ItemHomeLinearVerticalBinding binding, String dataBean, int position) {

        RecycleViewDivider horizontalDivider = new RecycleViewDivider(mContext,
                LinearLayoutManager.HORIZONTAL,
                CommonUtil.dip2px(mContext, 6),
                Color.parseColor("#2A2737"));
        binding.tagRecyclerView.addItemDecoration(horizontalDivider);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        binding.tagRecyclerView.setLayoutManager(layoutManager);
        HomeLinearTagAdapter tagAdapter = new HomeLinearTagAdapter(mContext);
        binding.tagRecyclerView.setAdapter(tagAdapter);
        tagAdapter.refreshData(CommonUtil.getTitles());

//        binding.titleView.setText(null);
//        GlideLoader.LoaderRadioCover(mContext, null, binding.coverView, 10);


    }
}
