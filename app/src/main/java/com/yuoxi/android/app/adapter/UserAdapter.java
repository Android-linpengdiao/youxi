package com.yuoxi.android.app.adapter;

import android.content.Context;

import com.base.utils.GlideLoader;
import com.base.view.OnClickListener;
import com.yuoxi.android.app.R;
import com.yuoxi.android.app.databinding.ItemUserBinding;
import com.yuoxi.android.app.databinding.ItemUserIconBinding;

/**
 * 首页 - 竖布局 - 标签
 */
public class UserAdapter extends BaseRecyclerAdapter<String, ItemUserBinding> {

    private OnClickListener onClickListener;

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public UserAdapter(Context context) {
        super(context);
    }

    @Override
    protected int getLayoutResId(int viewType) {
        return R.layout.item_user;
    }

    @Override
    protected void onBindItem(ItemUserBinding binding, String dataBean, int position) {

        GlideLoader.getInstance().LoaderDrawable(mContext, R.drawable.ic_test_user_icon, binding.userIconView);


    }
}
