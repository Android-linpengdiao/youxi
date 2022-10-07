package com.yuoxi.android.app.adapter;

import android.content.Context;
import android.view.View;

import com.base.UserInfo;
import com.base.utils.GlideLoader;
import com.base.view.OnClickListener;
import com.base.view.OnMultiClickListener;
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
        binding.functionContainer.setVisibility(View.GONE);
        binding.friendContainer.setVisibility(View.GONE);

        if (position == 0) {
            binding.functionContainer.setVisibility(View.VISIBLE);

        } else if (position == 1) {
            binding.userNameView.setText(dataBean.getName());
            binding.friendContainer.setVisibility(View.VISIBLE);
            binding.pinyinTextView.setText(dataBean.getPinyin().toUpperCase().substring(0, 1));
            binding.pinyinTextView.setVisibility(View.VISIBLE);

        } else {
            binding.userNameView.setText(dataBean.getName());
            binding.friendContainer.setVisibility(View.VISIBLE);
            if (!dataBean.getPinyin().substring(0, 1).equals(mList.get(position - 1).getPinyin().substring(0, 1))) {
                binding.pinyinTextView.setText(dataBean.getPinyin().toUpperCase().substring(0, 1));
                binding.pinyinTextView.setVisibility(View.VISIBLE);
            } else {
                binding.pinyinTextView.setVisibility(View.GONE);
            }
        }

        binding.friendContainer.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void OnMultiClick(View view) {
                if (onClickListener != null) {
                    onClickListener.onClick(view, dataBean);
                }
            }
        });
        binding.focusView.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void OnMultiClick(View view) {
                if (onClickListener != null) {
                    onClickListener.onClick(view, dataBean);
                }
            }
        });
        binding.fansView.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void OnMultiClick(View view) {
                if (onClickListener != null) {
                    onClickListener.onClick(view, dataBean);
                }
            }
        });
        binding.groupView.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void OnMultiClick(View view) {
                if (onClickListener != null) {
                    onClickListener.onClick(view, dataBean);
                }
            }
        });
        binding.blacklistView.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void OnMultiClick(View view) {
                if (onClickListener != null) {
                    onClickListener.onClick(view, dataBean);
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
