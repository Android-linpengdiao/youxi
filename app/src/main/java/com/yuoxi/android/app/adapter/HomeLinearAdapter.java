package com.yuoxi.android.app.adapter;

import android.content.Context;

import com.base.utils.GlideLoader;
import com.base.view.OnClickListener;
import com.yuoxi.android.app.R;
import com.yuoxi.android.app.databinding.ItemHomeLinearBinding;

/**
 * 发现 - 关注栏目
 */
public class HomeLinearAdapter extends BaseRecyclerAdapter<String, ItemHomeLinearBinding> {

    private OnClickListener onClickListener;

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public HomeLinearAdapter(Context context) {
        super(context);
    }

    @Override
    protected int getLayoutResId(int viewType) {
        return R.layout.item_home_linear;
    }

    @Override
    protected void onBindItem(ItemHomeLinearBinding binding, String dataBean, int position) {

//        binding.titleView.setText(null);
//        GlideLoader.LoaderRadioCover(mContext, null, binding.coverView, 10);


    }
}
