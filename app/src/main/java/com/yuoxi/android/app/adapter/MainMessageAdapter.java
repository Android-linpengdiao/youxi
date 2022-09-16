package com.yuoxi.android.app.adapter;

import android.content.Context;

import com.base.utils.GlideLoader;
import com.base.view.OnClickListener;
import com.yuoxi.android.app.R;
import com.yuoxi.android.app.databinding.ItemMainMessageBinding;
import com.yuoxi.android.app.databinding.ItemYuewanGridBinding;

/**
 * 消息 - 消息
 */
public class MainMessageAdapter extends BaseRecyclerAdapter<String, ItemMainMessageBinding> {

    private OnClickListener onClickListener;

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public MainMessageAdapter(Context context) {
        super(context);
    }

    @Override
    protected int getLayoutResId(int viewType) {
        return R.layout.item_main_message;
    }

    @Override
    protected void onBindItem(ItemMainMessageBinding binding, String dataBean, int position) {

//        binding.titleView.setText(null);
//        GlideLoader.getInstance().LoaderDrawable(mContext, R.drawable.ic_test_yingxiong, binding.userIconView);


    }
}
