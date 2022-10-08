package com.quakoo.im.adapter;

import android.content.Context;

import com.base.view.OnClickListener;
import com.quakoo.im.R;
import com.quakoo.im.databinding.ItemPluginTagBinding;
import com.quakoo.im.view.BaseRecyclerAdapter;

public class PluginTagAdapter extends BaseRecyclerAdapter<String, ItemPluginTagBinding> {

    private OnClickListener onClickListener;

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public PluginTagAdapter(Context context) {
        super(context);
    }

    @Override
    protected int getLayoutResId(int viewType) {
        return R.layout.item_plugin_tag;
    }

    @Override
    protected void onBindItem(ItemPluginTagBinding binding, String dataBean, int position) {
        binding.titleView.setText(dataBean);

    }
}