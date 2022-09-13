package com.yuoxi.android.app.view;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.yuoxi.android.app.R;


/**
 * @author
 * @package
 * @desc 评星
 * @date 2019/7/05 18:44
 * @copyright
 * @company
 */
public class RatingBar extends View {
    private static final String TAG = "AirRatingBar";
    /**
     * 默认，半个，全部选中
     */
    private Bitmap mRatingNormal, mRatingHalf, mRatingSelected;
    /**
     * 总数
     */
    private int mRatingTotalNum = 5;
    /**
     * 选中的数量
     */
    private float mRatingSelectedNumber;
    /**
     * 是否画全部
     */
    private Status status = Status.FULL;
    /**
     * 是否画全部
     * true 画全部
     * false 画半个
     */
    private boolean isFull;
    /**
     * 状态变化监听
     */
    private OnStarChangeListener mOnStarChangeListener;
    /**
     * 画笔
     */
    private Paint mPaint;
    /**
     * 间距
     */
    private int mRatingDistance;
    /**
     * 宽和高
     */
    private float mRatingWidth;
    private float mRatingHeight;
    /**
     * 是否可选中
     */
    private boolean selectable;

    public RatingBar(Context context) {
        this(context, null);
    }

    public RatingBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RatingBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initPaint();
        initTypeArray(context, attrs);
    }

    /**
     * 画笔
     */
    private void initPaint() {
        mPaint = new Paint();
    }

    /**
     * 属性
     *
     * @param context
     * @param attrs
     */
    private void initTypeArray(Context context, AttributeSet attrs) {
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.AirRatingBar);

        // 未选中的图片资源
        int starNormalId = array.getResourceId(R.styleable.AirRatingBar_ratingDefault, 0);
        if (starNormalId == 0) {
            throw new IllegalArgumentException("请设置属性 starNormal");
        }
        mRatingNormal = getBitmap(context, starNormalId);
        // 选中一半的图片资源
        int starHalfId = array.getResourceId(R.styleable.AirRatingBar_ratingHalf, 0);
        if (starHalfId != 0) {
            mRatingHalf = getBitmap(context, starHalfId);
        }
        // 选中全部的图片资源
        int starSelectedId = array.getResourceId(R.styleable.AirRatingBar_ratingSelect, 0);
        if (starSelectedId == 0) {
            throw new IllegalArgumentException("请设置属性 starSelected");
        }
        mRatingSelected = getBitmap(context, starSelectedId);
        // 如果没设置一半的图片资源，就用全部的代替
        if (starHalfId == 0) {
            mRatingHalf = mRatingSelected;
        }

        mRatingTotalNum = array.getInt(R.styleable.AirRatingBar_ratingTotalNum, mRatingTotalNum);
        mRatingSelectedNumber = array.getFloat(R.styleable.AirRatingBar_ratingDefaultSelectNum, mRatingSelectedNumber);
        mRatingDistance = (int) array.getDimension(R.styleable.AirRatingBar_ratingDistance, 0);
        mRatingWidth = array.getDimension(R.styleable.AirRatingBar_ratingWidth, 0);
        mRatingHeight = array.getDimension(R.styleable.AirRatingBar_ratingHeight, 0);
        isFull = array.getBoolean(R.styleable.AirRatingBar_ratingIsFull, true);
        selectable = array.getBoolean(R.styleable.AirRatingBar_ratingSelectable, false);
        array.recycle();

        // 如有指定宽高，获取最大值 去改变星星的大小（星星是正方形）
        int starWidth = (int) Math.max(mRatingWidth, mRatingHeight);
        if (starWidth > 0) {
            mRatingNormal = resetBitmap(mRatingNormal, starWidth);
            mRatingSelected = resetBitmap(mRatingSelected, starWidth);
            mRatingHalf = resetBitmap(mRatingHalf, starWidth);
        }

        // 计算一半还是全部（小数部分小于等于0.5就只是显示一半）
        if (!isFull) {
            int num = (int) mRatingSelectedNumber;
            if (mRatingSelectedNumber <= (num + 0.5f)) {
                status = Status.HALF;
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // 循环绘制
        for (int i = 0; i < mRatingTotalNum; i++) {
            float left = getPaddingLeft();
            // 从第二个星星开始，给它设置星星的间距
            if (i > 0) {
                left = getPaddingLeft() + i * (mRatingNormal.getWidth() + mRatingDistance);
            }
            float top = getPaddingTop();
            // 绘制选中的星星
            if (i < mRatingSelectedNumber) {
                // 比当前选中的数量小
                if (i < mRatingSelectedNumber - 1) {
                    canvas.drawBitmap(mRatingSelected, left, top, mPaint);
                } else {
                    // 在这里判断是不是要绘制满的
                    if (status == Status.FULL) {
                        canvas.drawBitmap(mRatingSelected, left, top, mPaint);
                    } else {
                        canvas.drawBitmap(mRatingHalf, left, top, mPaint);
                    }
                }
            } else {
                // 绘制正常的星星
                canvas.drawBitmap(mRatingNormal, left, top, mPaint);
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!selectable) {
            return super.onTouchEvent(event);
        }
        //减少绘制
        if (event.getAction() == MotionEvent.ACTION_MOVE) {
            // 获取用户触摸的x位置
            float x = event.getX();
            // 一个星星占的宽度
            int startWidth = getWidth() / mRatingTotalNum;
            // 计算用户触摸星星的位置
            int position = (int) (x / startWidth + 1);
            if (position < 0) {
                position = 0;
            }
            if (position > mRatingTotalNum) {
                position = mRatingTotalNum;
            }
            Status statu = Status.FULL;
            // 结果大于一半就是满的
            if (!isFull) {
                if ((mRatingSelectedNumber * 10) % 10 > 0 && (mRatingSelectedNumber * 10) % 10 <= 5) {
                    statu = Status.HALF;
                }
            }
            //减少绘制
            if (mRatingSelectedNumber != position || statu != status) {
                mRatingSelectedNumber = position;
                status = statu;
                invalidate();
                if (mOnStarChangeListener != null) {
                    position = (int) (mRatingSelectedNumber - 1);
                    // 选中的数量回调
                    float selectedNumber = status == Status.FULL ? mRatingSelectedNumber
                            : (mRatingSelectedNumber - 0.5f);
                    mOnStarChangeListener.onStarChanged(selectedNumber,
                            position < 0 ? 0 : position);
                }
            }
        } else if (event.getAction() == MotionEvent.ACTION_DOWN) {
            // 获取用户触摸的x位置
            float x = event.getX();
            // 一个星星占的宽度
            int startWidth = getWidth() / mRatingTotalNum;
            // 计算用户触摸星星的位置
            int position = (int) (x / startWidth + 1);
            if (position < 0) {
                position = 0;
            }
            if (position > mRatingTotalNum) {
                position = mRatingTotalNum;
            }
            Status statu = Status.FULL;
            // 结果大于一半就是满的
            if (!isFull) {
                if ((mRatingSelectedNumber * 10) % 10 > 0 && (mRatingSelectedNumber * 10) % 10 <= 5) {
                    statu = Status.HALF;
                }
            }
            //减少绘制
            if (mRatingSelectedNumber != position || statu != status) {
                mRatingSelectedNumber = position;
                status = statu;
                invalidate();
                if (mOnStarChangeListener != null) {
                    position = (int) (mRatingSelectedNumber - 1);
                    // 选中的数量回调
                    float selectedNumber = status == Status.FULL ? mRatingSelectedNumber
                            : (mRatingSelectedNumber - 0.5f);
                    mOnStarChangeListener.onStarChanged(selectedNumber,
                            position < 0 ? 0 : position);
                }
            }
        }
        return true;
    }

    /**
     * 如果用户设置了图片的宽高，就重新设置图片
     */
    public Bitmap resetBitmap(Bitmap bitMap, int startWidth) {
        // 得到新的图片
        return Bitmap.createScaledBitmap(bitMap, startWidth, startWidth, true);
    }

    /**
     * 如果用户设置了图片的宽高，就重新设置图片
     */
    public void resetSelectedBitmap(Bitmap bitMap, int startWidth) {
        // 得到新的图片
        mRatingSelected =  Bitmap.createScaledBitmap(bitMap, startWidth, startWidth, true);
        invalidate();
    }

    /**
     * 设置选中星星的数量
     */
    public void setSelectedNumber(int selectedNumber) {
        if (selectedNumber >= 0 && selectedNumber <= mRatingTotalNum) {
            this.mRatingSelectedNumber = selectedNumber;
            invalidate();
        }
    }

    /**
     * 设置星星的总数量
     */
    public void setStartTotalNumber(int startTotalNumber) {
        if (startTotalNumber > 0) {
            this.mRatingTotalNum = startTotalNumber;
            invalidate();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        // 用正常的一个星星图片去测量高
        int height = getPaddingTop() + getPaddingBottom() + mRatingNormal.getHeight();
        // 宽 = 星星的宽度*总数 + 星星的间距*（总数-1） +padding
        int width = getPaddingLeft() + getPaddingRight() + mRatingNormal.getWidth() * mRatingTotalNum + mRatingDistance * (mRatingTotalNum - 1);
        setMeasuredDimension(width, height);
    }

    /**
     * 获得bitmap
     *
     * @param context
     * @param vectorDrawableId
     * @return
     */
    private Bitmap getBitmap(Context context, int vectorDrawableId) {
        Bitmap bitmap;
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            Drawable vectorDrawable = context.getDrawable(vectorDrawableId);
            bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(),
                    vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            vectorDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            vectorDrawable.draw(canvas);
        } else {
            bitmap = BitmapFactory.decodeResource(context.getResources(), vectorDrawableId);
        }
        return bitmap;
    }

    public boolean isSelectable() {
        return selectable;
    }

    public void setSelectable(boolean selectable) {
        this.selectable = selectable;
    }

    private enum Status {
        /**
         * 全部 or 半个
         */
        FULL, HALF
    }

    public void setOnStarChangeListener(OnStarChangeListener onStarChangeListener) {
        mOnStarChangeListener = onStarChangeListener;
    }

    public interface OnStarChangeListener {
        /**
         *  状态变化回调
         * @param selectedNumber 选择数量
         * @param position       position
         */
        void onStarChanged(float selectedNumber, int position);
    }

    public float getSelectedNumber() {
        return mRatingSelectedNumber;
    }
}
