package com.yuoxi.android.app.adapter;

import android.content.Context;
import android.view.View;

import com.base.view.OnClickListener;
import com.base.view.OnMultiClickListener;
import com.yuoxi.android.app.R;
import com.yuoxi.android.app.databinding.ItemSwGoldBinding;
import com.yuoxi.android.app.databinding.ItemSwTaskBinding;

public class SWTaskAdapter extends BaseRecyclerAdapter<String, ItemSwTaskBinding> {


    private OnClickListener onClickListener;

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public SWTaskAdapter(Context context) {
        super(context);
    }

    @Override
    protected int getLayoutResId(int viewType) {
        return R.layout.item_sw_task;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    protected void onBindItem(ItemSwTaskBinding binding, String dataBean, int position) {
        binding.confirmView.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void OnMultiClick(View view) {
                if (onClickListener != null) {
                    onClickListener.onClick(view, dataBean);
                }
            }
        });
    }
}
