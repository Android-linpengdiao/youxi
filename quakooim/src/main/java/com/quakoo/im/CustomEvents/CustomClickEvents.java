package com.quakoo.im.CustomEvents;

import android.view.View;

/**
 * 自定义的点击事件
 */
public interface CustomClickEvents {
    void Click(View view, String tag);//接口其实应该只要一个String类型就可以区分,但考虑到有可能需要View进行处理,所以需要将点击触发的控件回传
}
