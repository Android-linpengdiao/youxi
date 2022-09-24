package com.quakoo.im.view;

import android.content.Context;
import android.util.AttributeSet;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class CustomLayoutManager extends LinearLayoutManager {
    private RecyclerView.Adapter mAdapter;

    public CustomLayoutManager(Context context) {
        super(context);
    }

    public CustomLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
    }

    public CustomLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    //    scrollToPositionWithOffset
    public void setAdapter(RecyclerView.Adapter adapter) {
        mAdapter = adapter;
    }

    @Override
    public void scrollToPositionWithOffset(int position, int offset) {
        if (mAdapter != null && mAdapter.getItemCount() > 0) {
            if (mAdapter.getItemCount() - this.findLastVisibleItemPosition() <= 2) {
                super.scrollToPosition(mAdapter.getItemCount()-1);
            }
        } else {
            super.scrollToPositionWithOffset(position, offset);
        }
    }

    public void scrollToPositionWithOffset(int position, int offset, boolean f) {
        if (f) {
            if (mAdapter != null && mAdapter.getItemCount() > 0) {
                super.scrollToPositionWithOffset(mAdapter.getItemCount()-1, offset);
            }
        }else{
            super.scrollToPositionWithOffset(position, offset);
        }

    }
}
