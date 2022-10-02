package com.yuoxi.android.app.adapter;

import android.content.Context;

import com.base.utils.GlideLoader;
import com.base.view.OnClickListener;
import com.yuoxi.android.app.R;
import com.yuoxi.android.app.databinding.ItemHomeLinearHorizontalBinding;
import com.yuoxi.android.app.databinding.ItemJubenPersonHorizontalBinding;

/**
 * 发现 - 横布局
 */
public class JuBenPersonHorizontalAdapter extends BaseRecyclerAdapter<String, ItemJubenPersonHorizontalBinding> {

    private OnClickListener onClickListener;

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public JuBenPersonHorizontalAdapter(Context context) {
        super(context);
    }

    @Override
    protected int getLayoutResId(int viewType) {
        return R.layout.item_juben_person_horizontal;
    }

    @Override
    protected void onBindItem(ItemJubenPersonHorizontalBinding binding, String dataBean, int position) {

//        binding.titleView.setText(null);
        GlideLoader.getInstance().LoaderDrawable(mContext, R.drawable.ic_test_cover, binding.coverView);


    }
}
