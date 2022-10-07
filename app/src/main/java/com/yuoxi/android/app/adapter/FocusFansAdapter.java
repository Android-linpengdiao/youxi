package com.yuoxi.android.app.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.View;

import com.base.utils.CommonUtil;
import com.base.utils.GlideLoader;
import com.base.view.OnClickListener;
import com.yuoxi.android.app.R;
import com.yuoxi.android.app.databinding.ItemFocusFansBinding;
import com.yuoxi.android.app.model.FocusFansBean;

public class FocusFansAdapter extends BaseRecyclerAdapter<String, ItemFocusFansBinding> {


    private OnClickListener onClickListener;
    private int type = 0;//0-关注  1-粉丝  2-赠送

    public void setType(int type) {
        this.type = type;
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public FocusFansAdapter(Context context) {
        super(context);
    }

    @Override
    protected int getLayoutResId(int viewType) {
        return R.layout.item_focus_fans;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    protected void onBindItem(ItemFocusFansBinding binding, String dataBean, int position) {

        binding.focusView.setText(type == 0 ? "取关" :type == 1 ? "回关" :type == 2 ? "赠送" : "已关注");

//        if (type == 0) {
//            binding.titleView.setText(!CommonUtil.isBlank(dataBean.getTypeUser().getName()) ? dataBean.getTypeUser().getName() : dataBean.getTypeUser().getPhone());
//            GlideLoader.getInstance().LoaderUserIcon(mContext, dataBean.getTypeUser().getIcon(), binding.iconView);
//        } else if (type == 1) {
//            binding.titleView.setText(!CommonUtil.isBlank(dataBean.getUser().getName()) ? dataBean.getUser().getName() : dataBean.getUser().getPhone());
//            GlideLoader.getInstance().LoaderUserIcon(mContext, dataBean.getUser().getIcon(), binding.iconView);
//        }
//
//        binding.focusView.setVisibility(type == 2 ? View.GONE : View.VISIBLE);
//        if (dataBean.getFocus() == 1) {
//            binding.focusView.setText("已关注");
//            binding.focusView.setTextColor(Color.parseColor("#333333"));
//            binding.focusView.setBackgroundResource(R.drawable.button_radius_gray);
//        } else {
//            binding.focusView.setText("关注");
//            binding.focusView.setTextColor(Color.parseColor("#ffffff"));
//            binding.focusView.setBackgroundResource(R.drawable.button_yellow);
//        }
//
        binding.focusView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onClickListener != null) {
                    onClickListener.onClick(view, dataBean);
                }
            }
        });
        binding.getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onClickListener != null) {
                    onClickListener.onClick(view, dataBean);
                }
            }
        });
    }
}
