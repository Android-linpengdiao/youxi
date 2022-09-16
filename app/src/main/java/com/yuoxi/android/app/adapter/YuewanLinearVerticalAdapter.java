package com.yuoxi.android.app.adapter;

import android.content.Context;
import android.graphics.Color;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.base.utils.CommonUtil;
import com.base.utils.GlideLoader;
import com.base.view.OnClickListener;
import com.base.view.RecycleViewDivider;
import com.yuoxi.android.app.R;
import com.yuoxi.android.app.databinding.ItemHomeLinearVerticalBinding;
import com.yuoxi.android.app.databinding.ItemYuewanLinearVerticalBinding;

/**
 * 首页 - 竖布局
 */
public class YuewanLinearVerticalAdapter extends BaseRecyclerAdapter<String, ItemYuewanLinearVerticalBinding> {

    private OnClickListener onClickListener;

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public YuewanLinearVerticalAdapter(Context context) {
        super(context);
    }

    @Override
    protected int getLayoutResId(int viewType) {
        return R.layout.item_yuewan_linear_vertical;
    }

    @Override
    protected void onBindItem(ItemYuewanLinearVerticalBinding binding, String dataBean, int position) {

//        binding.titleView.setText(null);
        GlideLoader.getInstance().LoaderDrawable(mContext, R.drawable.ic_test_cover, binding.coverView);


    }
}
