package com.yuoxi.android.app.adapter;

import android.content.Context;
import android.view.View;

import com.base.utils.GlideLoader;
import com.base.view.OnClickListener;
import com.base.view.OnMultiClickListener;
import com.yuoxi.android.app.R;
import com.yuoxi.android.app.databinding.ItemHomeLinearHorizontalBinding;

/**
 * 发现 - 横布局
 */
public class HomeLinearHorizontalAdapter extends BaseRecyclerAdapter<String, ItemHomeLinearHorizontalBinding> {

    private OnClickListener onClickListener;

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public HomeLinearHorizontalAdapter(Context context) {
        super(context);
    }

    @Override
    protected int getLayoutResId(int viewType) {
        return R.layout.item_home_linear_horizontal;
    }

    @Override
    protected void onBindItem(ItemHomeLinearHorizontalBinding binding, String dataBean, int position) {

//        binding.titleView.setText(null);
        GlideLoader.getInstance().LoaderDrawable(mContext, R.drawable.ic_test_cover, binding.coverView);
        binding.getRoot().setOnClickListener(new OnMultiClickListener() {
            @Override
            public void OnMultiClick(View view) {
                if (onClickListener != null) {
                    onClickListener.onClick(view, dataBean);
                }
            }
        });

    }
}
