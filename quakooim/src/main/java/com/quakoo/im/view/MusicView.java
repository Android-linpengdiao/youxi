package com.quakoo.im.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

import com.base.utils.CommonUtil;

import java.util.Random;

public class MusicView extends View {

    private Context context;

    private Paint paint; //定义一个画笔
    private int width;

    private int rectWidth;//矩形的宽
    private int rectHeight;//矩形的高
    private int offset = 10;//两个矩形之间的距离

    private LinearGradient linearGradient; //渲染渐变

    //音频条的数目
    private int rectCount;

    public MusicView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initView();
    }

    private void initView() {
        paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.FILL);//设置画笔的样式为填充或者边框
        paint.setStrokeCap(Paint.Cap.ROUND); //圆角效果
        rectCount = 5;//矩形单的数目

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = getWidth();//获取屏幕的宽度
        rectHeight = getHeight();//获取屏幕的高度
        rectWidth = (int) (width / rectCount);//每个音频条的宽度
        offset = (getWidth() / rectCount - CommonUtil.dip2px(context, 4)) / 2;
        linearGradient = new LinearGradient(0, 0, getWidth(), getHeight(), Color.WHITE, Color.WHITE, Shader.TileMode.CLAMP);
        paint.setShader(linearGradient);


    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        for (int i = 0; i < rectCount; i++) {
            int height = new Random().nextInt(getHeight() / 3);
            canvas.drawRect((int) (i * rectWidth + offset), height, i * rectWidth + offset + CommonUtil.dip2px(context, 4), getHeight() - height, paint);
        }
        postInvalidateDelayed(100); //隔300毫秒刷新一次

    }
}