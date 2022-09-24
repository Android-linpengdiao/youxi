package com.quakoo.im.model;

import android.graphics.Bitmap;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

/**
 *聊天窗口下 图片,相机名片实体 用于存放name,Bitmap
 */
public class ChatBottomIocnModel extends BaseObservable {
    private String name;
    private Bitmap bitmap;


    @Bindable
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    @Bindable
    public Bitmap getBitmap() {
        return bitmap;
    }
    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }


}
