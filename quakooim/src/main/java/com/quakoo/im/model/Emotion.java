package com.quakoo.im.model;


import android.graphics.Bitmap;

public class Emotion {
    private String Key;
    private Bitmap bitmap;
    private String Value;


    public String getKey() {
        return Key;
    }

    public void setKey(String key) {
        Key = key;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public String getValue() {
        return Value;
    }

    public void setValue(String value) {
        Value = value;
    }
}
