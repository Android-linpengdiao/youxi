package com.yuoxi.android.app.adapter;

import android.content.Context;

import com.base.utils.GlideLoader;
import com.base.view.OnClickListener;
import com.yuoxi.android.app.R;
import com.yuoxi.android.app.databinding.ItemCommentBinding;
import com.yuoxi.android.app.databinding.ItemCommentListBinding;

/**
 * 发现 - 横布局
 */
public class CommentListAdapter extends BaseRecyclerAdapter<String, ItemCommentListBinding> {

    private OnClickListener onClickListener;

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public CommentListAdapter(Context context) {
        super(context);
    }

    @Override
    protected int getLayoutResId(int viewType) {
        return R.layout.item_comment_list;
    }

    @Override
    protected void onBindItem(ItemCommentListBinding binding, String dataBean, int position) {

//        binding.titleView.setText(null);
        GlideLoader.getInstance().LoaderDrawable(mContext, R.drawable.ic_test_user_icon, binding.coverView);


    }
}
