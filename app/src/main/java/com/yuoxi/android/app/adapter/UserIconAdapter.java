package com.yuoxi.android.app.adapter;

import android.content.Context;

import com.base.utils.GlideLoader;
import com.base.view.OnClickListener;
import com.yuoxi.android.app.R;
import com.yuoxi.android.app.databinding.ItemHomeLinearTagBinding;
import com.yuoxi.android.app.databinding.ItemUserIconBinding;

/**
 * 首页 - 竖布局 - 标签
 */
public class UserIconAdapter extends BaseRecyclerAdapter<String, ItemUserIconBinding> {

    private OnClickListener onClickListener;

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public UserIconAdapter(Context context) {
        super(context);
    }

    @Override
    protected int getLayoutResId(int viewType) {
        return R.layout.item_user_icon;
    }

    @Override
    protected void onBindItem(ItemUserIconBinding binding, String dataBean, int position) {

        GlideLoader.getInstance().LoaderDrawable(mContext, R.drawable.ic_test_user_icon, binding.userIconView);


    }
}
