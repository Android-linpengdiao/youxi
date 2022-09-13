package com.base.manager;

import android.content.Context;
import android.graphics.Color;
import android.widget.RelativeLayout;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.base.utils.CommonUtil;
import com.base.view.RecycleViewDivider;

/**
 * Created by Administrator on 2018/9/19.
 */

public class RecycleViewManager {


    public static void setVideoLayoutManager(Context context, RecyclerView recyclerView, int size) {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        if (size > 1) {
            RecycleViewDivider divider = new RecycleViewDivider(context,
                    LinearLayoutManager.HORIZONTAL,
                    CommonUtil.dip2px(context, 4),
                    Color.parseColor("#FAFAFA"));
            recyclerView.addItemDecoration(divider);
            linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
            recyclerView.setNestedScrollingEnabled(false);
            recyclerView.setLayoutManager(linearLayoutManager);
            if (size > 2) {
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) recyclerView.getLayoutParams();
                layoutParams.rightMargin = 0;
            }

        } else {
            recyclerView.setLayoutManager(new LinearLayoutManager(context));

        }
    }


    /**
     * 滑动到指定位置
     * <p>
     * 不具有平滑滚动视觉效果:
     */
    public static void smoothMoveToPosition(RecyclerView mRecyclerView, int position) {

        if (position != -1) {
            mRecyclerView.scrollToPosition(position);
            LinearLayoutManager mLayoutManager = (LinearLayoutManager) mRecyclerView.getLayoutManager();
            mLayoutManager.scrollToPositionWithOffset(position, 0);
        }
    }

    /**
     * 滑动到指定位置
     * <p>
     * 具有平滑滚动视觉效果:
     */
    public static void smoothMoveToPositionAnim(RecyclerView mRecyclerView, int position) {
        if (position != -1) {
            //目标项是否在最后一个可见项之后
            boolean mShouldScroll;
            //记录目标项位置
            int mToPosition;

            // 第一个可见位置
            int firstItem = mRecyclerView.getChildLayoutPosition(mRecyclerView.getChildAt(0));
            // 最后一个可见位置
            int lastItem = mRecyclerView.getChildLayoutPosition(mRecyclerView.getChildAt(mRecyclerView.getChildCount() - 1));
            if (position < firstItem) {
                // 第一种可能:跳转位置在第一个可见位置之前
                mRecyclerView.smoothScrollToPosition(position);
            } else if (position <= lastItem) {
                // 第二种可能:跳转位置在第一个可见位置之后
                int movePosition = position - firstItem;
                if (movePosition >= 0 && movePosition < mRecyclerView.getChildCount()) {
                    int top = mRecyclerView.getChildAt(movePosition).getTop();
                    mRecyclerView.smoothScrollBy(0, top);
                }
            } else {
                // 第三种可能:跳转位置在最后可见项之后
                mRecyclerView.smoothScrollToPosition(position);
                mToPosition = position;
                mShouldScroll = true;
            }
        }
    }
}
