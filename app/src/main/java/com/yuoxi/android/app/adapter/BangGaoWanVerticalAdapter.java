package com.yuoxi.android.app.adapter;

import android.content.Context;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.base.utils.CommonUtil;
import com.base.utils.GlideLoader;
import com.base.view.OnClickListener;
import com.yuoxi.android.app.R;
import com.yuoxi.android.app.databinding.ItemBangGaowanBinding;
import com.yuoxi.android.app.databinding.ItemBangJubenBinding;

/**
 * 榜单 - 高玩
 */
public class BangGaoWanVerticalAdapter extends BaseRecyclerAdapter<String, ItemBangGaowanBinding> {

    private OnClickListener onClickListener;

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public BangGaoWanVerticalAdapter(Context context) {
        super(context);
    }

    @Override
    protected int getLayoutResId(int viewType) {
        return R.layout.item_bang_gaowan;
    }

    @Override
    protected void onBindItem(ItemBangGaowanBinding binding, String dataBean, int position) {
        GlideLoader.getInstance().LoaderDrawable(mContext, R.drawable.ic_test_cover, binding.coverView, 30);
        binding.indexView.setText(String.valueOf(position + 4));
        binding.getRoot().setBackgroundResource(position == 0 ? R.mipmap.ic_bang_gaowan_4 : position == 1 ? R.mipmap.ic_bang_gaowan_5: position == 5 ? R.mipmap.ic_bang_gaowan_bang : R.mipmap.ic_bang_gaowan_6);


    }
}
