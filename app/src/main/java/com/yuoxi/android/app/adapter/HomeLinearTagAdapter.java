package com.yuoxi.android.app.adapter;

import android.content.Context;

import com.base.view.OnClickListener;
import com.yuoxi.android.app.R;
import com.yuoxi.android.app.databinding.ItemHomeLinearTagBinding;
import com.yuoxi.android.app.databinding.ItemHomeLinearVerticalBinding;

/**
 * 首页 - 竖布局 - 标签
 */
public class HomeLinearTagAdapter extends BaseRecyclerAdapter<String, ItemHomeLinearTagBinding> {

    private OnClickListener onClickListener;

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public HomeLinearTagAdapter(Context context) {
        super(context);
    }

    @Override
    protected int getLayoutResId(int viewType) {
        return R.layout.item_home_linear_tag;
    }

    @Override
    protected void onBindItem(ItemHomeLinearTagBinding binding, String dataBean, int position) {

//        binding.titleView.setText(null);
//        GlideLoader.LoaderRadioCover(mContext, null, binding.coverView, 10);


    }
}
