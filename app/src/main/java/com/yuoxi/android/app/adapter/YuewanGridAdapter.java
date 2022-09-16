package com.yuoxi.android.app.adapter;

import android.content.Context;
import android.view.View;

import com.base.utils.GlideLoader;
import com.base.view.OnClickListener;
import com.base.view.OnMultiClickListener;
import com.yuoxi.android.app.R;
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
        GlideLoader.getInstance().LoaderDrawable(mContext, R.drawable.ic_test_yingxiong, binding.coverView);
        binding.getRoot().setOnClickListener(new OnMultiClickListener() {
            @Override
            public void OnMultiClick(View view) {
                if (onClickListener != null)
                    onClickListener.onClick(view, dataBean);
            }
        });

    }
}
