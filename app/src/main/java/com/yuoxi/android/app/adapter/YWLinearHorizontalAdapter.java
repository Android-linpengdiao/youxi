package com.yuoxi.android.app.adapter;

import android.content.Context;

import com.base.utils.GlideLoader;
import com.base.view.OnClickListener;
import com.yuoxi.android.app.R;
import com.yuoxi.android.app.databinding.ItemHomeLinearHorizontalBinding;
import com.yuoxi.android.app.databinding.ItemYwLinearHorizontalBinding;

/**
 * 约玩 - 横布局
 */
public class YWLinearHorizontalAdapter extends BaseRecyclerAdapter<String, ItemYwLinearHorizontalBinding> {

    private OnClickListener onClickListener;

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public YWLinearHorizontalAdapter(Context context) {
        super(context);
    }

    @Override
    protected int getLayoutResId(int viewType) {
        return R.layout.item_yw_linear_horizontal;
    }

    @Override
    protected void onBindItem(ItemYwLinearHorizontalBinding binding, String dataBean, int position) {

        if (position==0){
            binding.titleView.setBackgroundResource(R.drawable.button_mg);
            binding.coverView.setBackgroundResource(R.drawable.button_stroke_mg);
        }else {
            binding.titleView.setBackgroundResource(R.color.transparent);
            binding.coverView.setBackgroundResource(R.color.transparent);
        }
//        binding.titleView.setText(null);
        GlideLoader.getInstance().LoaderDrawable(mContext, R.drawable.ic_test_yingxiong, binding.coverView, 100);


    }
}
