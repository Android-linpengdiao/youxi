package com.yuoxi.android.app.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.View;

import com.base.view.OnClickListener;
import com.yuoxi.android.app.R;
import com.yuoxi.android.app.databinding.ItemFilterTypeBinding;
import com.yuoxi.android.app.databinding.ItemHomeLinearTagBinding;

/**
 * 案件室 - 筛选
 */
public class FilterTypeAdapter extends BaseRecyclerAdapter<String, ItemFilterTypeBinding> {

    private int mPosition = 0;
    private OnClickListener onClickListener;

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public FilterTypeAdapter(Context context) {
        super(context);
    }

    @Override
    protected int getLayoutResId(int viewType) {
        return R.layout.item_filter_type;
    }

    @Override
    protected void onBindItem(ItemFilterTypeBinding binding, String dataBean, int position) {

        binding.titleView.setTextColor(mPosition == position ? Color.parseColor("#ffffff") : Color.parseColor("#909090"));
        binding.getRoot().setBackgroundResource(mPosition == position ? R.drawable.button_white_15_t10 : R.drawable.bg_transparent);
        binding.getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPosition = position;
                notifyDataSetChanged();
            }
        });


    }
}
