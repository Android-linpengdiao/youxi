package com.yuoxi.android.app.tab;

import android.annotation.SuppressLint;
import android.os.Build;

import androidx.annotation.NonNull;

class ViewUtils {

    @SuppressLint("ObsoleteSdkInt")
    private static final ValueAnimatorCompat.Creator DEFAULT_ANIMATOR_CREATOR
            = new ValueAnimatorCompat.Creator() {
        @NonNull
        @Override
        public ValueAnimatorCompat createAnimator() {
            return new ValueAnimatorCompat(Build.VERSION.SDK_INT >= 12
                    ? new ValueAnimatorCompatImplHoneycombMr1()
                    : new ValueAnimatorCompatImplGingerbread());
        }
    };

    static ValueAnimatorCompat createAnimator() {
        return DEFAULT_ANIMATOR_CREATOR.createAnimator();
    }

}
