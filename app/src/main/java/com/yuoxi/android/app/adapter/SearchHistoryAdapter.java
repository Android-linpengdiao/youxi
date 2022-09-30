package com.yuoxi.android.app.adapter;

import android.content.Context;
import android.view.View;

import com.base.view.OnClickListener;
import com.yuoxi.android.app.R;
import com.yuoxi.android.app.databinding.ItemSearchHistoryBinding;


public class SearchHistoryAdapter extends BaseRecyclerAdapter<String, ItemSearchHistoryBinding> {

    private OnClickListener onClickListener;

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public SearchHistoryAdapter(Context context) {
        super(context);
    }

    @Override
    protected int getLayoutResId(int viewType) { //应该在此根viewType 选择不同布局的,但设计图上给的差距不大.就整合成一个布局了
        return R.layout.item_search_history;
    }

    @Override
    protected void onBindItem(ItemSearchHistoryBinding binding, String string, int position) {
        binding.content.setText(string);
        binding.getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onClickListener != null)
                    onClickListener.onClick(view, string);
            }
        });

    }
}
