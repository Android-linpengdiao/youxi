package com.yuoxi.android.app.adapter;

import android.content.Context;

import com.base.view.OnClickListener;
import com.yuoxi.android.app.R;
import com.yuoxi.android.app.databinding.ItemHomeLinearHorizontalBinding;
import com.yuoxi.android.app.databinding.ItemYuewanGridBinding;

/**
 * 约玩 - 网格布局
 */
public class YuewanGridAdapter extends BaseRecyclerAdapter<String, ItemYuewanGridBinding> {

    private OnClickListener onClickListener;

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public YuewanGridAdapter(Context context) {
        super(context);
    }

    @Override
    protected int getLayoutResId(int viewType) {
        return R.layout.item_yuewan_grid;
    }

    @Override
    protected void onBindItem(ItemYuewanGridBinding binding, String dataBean, int position) {

//        binding.titleView.setText(null);
//        GlideLoader.LoaderRadioCover(mContext, null, binding.coverView, 10);


    }
}
