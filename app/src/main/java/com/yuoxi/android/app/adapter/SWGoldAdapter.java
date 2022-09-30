package com.yuoxi.android.app.adapter;

import android.content.Context;

import com.base.view.OnClickListener;
import com.yuoxi.android.app.R;
import com.yuoxi.android.app.databinding.ItemFocusFansBinding;
import com.yuoxi.android.app.databinding.ItemSwGoldBinding;

public class SWGoldAdapter extends BaseRecyclerAdapter<String, ItemSwGoldBinding> {


    private OnClickListener onClickListener;

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public SWGoldAdapter(Context context) {
        super(context);
    }

    @Override
    protected int getLayoutResId(int viewType) {
        return R.layout.item_sw_gold;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    protected void onBindItem(ItemSwGoldBinding binding, String dataBean, int position) {

    }
}
