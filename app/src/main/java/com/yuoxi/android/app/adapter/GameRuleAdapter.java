package com.yuoxi.android.app.adapter;

import android.content.Context;
import android.view.View;

import com.base.utils.GlideLoader;
import com.base.view.OnClickListener;
import com.yuoxi.android.app.R;
import com.yuoxi.android.app.databinding.ItemBlacklistBinding;
import com.yuoxi.android.app.databinding.ItemGameRuleBinding;

public class GameRuleAdapter extends BaseRecyclerAdapter<String, ItemGameRuleBinding> {


    private OnClickListener onClickListener;

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public GameRuleAdapter(Context context) {
        super(context);
    }

    @Override
    protected int getLayoutResId(int viewType) {
        return R.layout.item_game_rule;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    protected void onBindItem(ItemGameRuleBinding binding, String dataBean, int position) {
        GlideLoader.getInstance().LoaderImage(mContext, "https://t7.baidu.com/it/u=2371362259,3988640650&fm=193&f=GIF", binding.coverView);
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
