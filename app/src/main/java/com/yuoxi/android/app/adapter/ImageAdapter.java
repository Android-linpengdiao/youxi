package com.yuoxi.android.app.adapter;

import android.content.Context;
import android.view.View;

import com.base.utils.GlideLoader;
import com.base.view.OnClickListener;
import com.yuoxi.android.app.R;
import com.yuoxi.android.app.databinding.ItemImageBinding;

public class ImageAdapter extends BaseRecyclerAdapter<String, ItemImageBinding> {

    private OnClickListener onClickListener;

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public ImageAdapter(Context context) {
        super(context);
    }

    @Override
    protected int getLayoutResId(int viewType) { //应该在此根viewType 选择不同布局的,但设计图上给的差距不大.就整合成一个布局了
        return R.layout.item_image;
    }

    @Override
    protected void onBindItem(final ItemImageBinding binding, String url, int position) {
        if (url != null && url.equals("add")) {
            binding.deleteView.setVisibility(View.GONE);
            binding.editContainer.setVisibility(View.VISIBLE);
            GlideLoader.getInstance().LoaderDrawable(mContext, R.drawable.bg_transparent, binding.imageView, 10);
        } else {
            binding.deleteView.setVisibility(View.VISIBLE);
            binding.editContainer.setVisibility(View.GONE);
            GlideLoader.getInstance().LoaderImage(mContext, url, binding.imageView, 10);
        }
        binding.deleteView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mList.remove(position);
                notifyDataSetChanged();
            }
        });
        binding.viewLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onClickListener != null) {
                    onClickListener.onClick(view, position);
                }
            }
        });

    }
}
