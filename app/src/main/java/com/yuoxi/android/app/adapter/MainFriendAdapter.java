package com.yuoxi.android.app.adapter;

import android.content.Context;
import android.view.View;

import com.base.UserInfo;
import com.base.utils.GlideLoader;
import com.base.view.OnClickListener;
import com.yuoxi.android.app.R;
import com.yuoxi.android.app.databinding.ItemMainFriendBinding;
import com.yuoxi.android.app.databinding.ItemMainMessageBinding;

/**
 * 消息 - 好友
 */
public class MainFriendAdapter extends BaseRecyclerAdapter<UserInfo, ItemMainFriendBinding> {

    private OnClickListener onClickListener;

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public MainFriendAdapter(Context context) {
        super(context);
    }

    @Override
    protected int getLayoutResId(int viewType) {
        return R.layout.item_main_friend;
    }

    @Override
    protected void onBindItem(ItemMainFriendBinding binding, UserInfo dataBean, int position) {
        binding.userNameView.setText(dataBean.getName());

        if (position != 0) {
            if (!dataBean.getPinyin().substring(0, 1).equals(mList.get(position - 1).getPinyin().substring(0, 1))) {
                binding.pinyinTextView.setText(dataBean.getPinyin().toUpperCase().substring(0, 1));
                binding.pinyinTextView.setVisibility(View.VISIBLE);
            } else {
                binding.pinyinTextView.setVisibility(View.GONE);
            }
        } else {
            binding.pinyinTextView.setText(dataBean.getPinyin().toUpperCase().substring(0, 1));
            binding.pinyinTextView.setVisibility(View.VISIBLE);
        }

        binding.viewLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onClickListener != null) {
                    onClickListener.onClick(v, dataBean);
                }
            }
        });
    }

    public int getLetterPosition(String letter) {
        Integer integer = null;
        for (int i = 0; i < mList.size(); i++) {
            if (mList.get(i).getPinyin().substring(0, 1).equalsIgnoreCase(letter.substring(0, 1))) {
                integer = i;
                return integer;
            }
        }
        return integer == null ? -1 : integer;
    }
}
