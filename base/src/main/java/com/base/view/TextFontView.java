package com.base.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.base.BaseApplication;
import com.base.R;
import com.base.utils.SharedPreferencesUtils;

@SuppressLint("AppCompatCustomView")
public class TextFontView extends TextView {

    public TextFontView(Context context) {
        super(context);
    }

    public TextFontView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public TextFontView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        Typeface typeface = BaseApplication.getInstance().getTypeface();
        setTypeface(typeface);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

}
