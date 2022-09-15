package com.yuoxi.android.app.tab;

class MathUtils {
    static float constrain(float amount, float low, float high) {
        return amount < low ? low : (amount > high ? high : amount);
    }
}
