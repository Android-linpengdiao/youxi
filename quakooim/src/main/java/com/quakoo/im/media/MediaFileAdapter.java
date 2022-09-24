package com.quakoo.im.media;

import android.content.Context;
import android.graphics.Color;
import android.view.View;

import com.base.utils.CommonUtil;
import com.base.utils.GlideLoader;
import com.base.utils.ToastUtils;
import com.base.view.OnClickListener;
import com.quakoo.im.R;
import com.quakoo.im.databinding.ItemMediaFileBinding;
import com.quakoo.im.view.BaseRecyclerAdapter;

public class MediaFileAdapter extends BaseRecyclerAdapter<MediaFile, ItemMediaFileBinding> {

    private int maxNumber = 1;
    private boolean complete = false;
    private OnClickListener onClickListener;

    public void setMaxNumber(int maxNumber) {
        this.maxNumber = maxNumber;
    }

    public void setComplete(boolean complete) {
        this.complete = complete;
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public MediaFileAdapter(Context context) {
        super(context);
    }

    @Override
    protected int getLayoutResId(int viewType) {
        return R.layout.item_media_file;
    }


    @Override
    protected void onBindItem(ItemMediaFileBinding binding, MediaFile dataBean, int position) {
        GlideLoader.getInstance().LoaderMediaImage(mContext, dataBean.getPath(), binding.coverView);
        binding.selectView.setSelected(dataBean.getStatus() == 0 ? false : true);
        binding.durationView.setText(CommonUtil.FormatMiss(dataBean.getDuration() / 1000));
        binding.durationView.setVisibility(dataBean.getType() == MediaFile.VIDEO ? View.VISIBLE : View.GONE);
        binding.backgroundView.setBackgroundColor(dataBean.getStatus() == 0 ? Color.parseColor("#0D000000") : Color.parseColor("#80000000"));
        binding.selectView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (complete && dataBean.getStatus() == 0) {
                    ToastUtils.showShort(mContext, "你最多只能选择" + maxNumber + "个图片");
                    return;
                }
                dataBean.setStatus(dataBean.getStatus() == 0 ? 1 : 0);
                binding.selectView.setSelected(dataBean.getStatus() == 0 ? false : true);
                binding.backgroundView.setBackgroundColor(dataBean.getStatus() == 0 ? Color.parseColor("#0D000000") : Color.parseColor("#80000000"));
                if (onClickListener != null)
                    onClickListener.onClick(view, dataBean);
            }
        });
        binding.coverView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onClickListener != null)
                    onClickListener.onClick(view, dataBean);
            }
        });
    }
}
