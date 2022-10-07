package com.yuoxi.android.app.adapter;

import android.content.Context;

import com.base.utils.GlideLoader;
import com.base.view.OnClickListener;
import com.yuoxi.android.app.R;
import com.yuoxi.android.app.databinding.ItemCommentBinding;
import com.yuoxi.android.app.databinding.ItemOrderBinding;

/**
 * 发现 - 横布局
 */
public class OrderAdapter extends BaseRecyclerAdapter<String, ItemOrderBinding> {

    private OnClickListener onClickListener;

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public OrderAdapter(Context context) {
        super(context);
    }

    @Override
    protected int getLayoutResId(int viewType) {
        return R.layout.item_order;
    }

    @Override
    protected void onBindItem(ItemOrderBinding binding, String dataBean, int position) {


    }
}
