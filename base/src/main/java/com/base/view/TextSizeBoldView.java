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
public class TextSizeBoldView extends TextView {

    private float textSize;

    public TextSizeBoldView(Context context) {
        super(context);
    }

    public TextSizeBoldView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public TextSizeBoldView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.TextSizeView);
        textSize = typedArray.getDimensionPixelSize(R.styleable.TextSizeView_text_size, 0);
        setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize + getConfigTextSize());
        boolean bold = typedArray.getBoolean(R.styleable.TextSizeView_text_bold, false);
        Typeface typeface = BaseApplication.getInstance().getTypefaceBold();
        setTypeface(typeface);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    private float getConfigTextSize() {
        float textSize = SharedPreferencesUtils.getInstance().getTextSize();
        if (textSize == 0) {
            return getContext().getResources().getDimensionPixelSize(R.dimen.sp_m_2);

        } else if (textSize == 1) {
            return getContext().getResources().getDimensionPixelSize(R.dimen.sp_0);

        } else if (textSize == 2) {
            return getContext().getResources().getDimensionPixelSize(R.dimen.sp_4);

        } else if (textSize == 3) {
            return getContext().getResources().getDimensionPixelSize(R.dimen.sp_8);

        } else if (textSize == 4) {
            return getContext().getResources().getDimensionPixelSize(R.dimen.sp_12);

        }
        return 0;
    }
}
