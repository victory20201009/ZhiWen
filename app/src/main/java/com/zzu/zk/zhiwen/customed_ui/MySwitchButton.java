package com.zzu.zk.zhiwen.customed_ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.zzu.zk.zhiwen.R;

public class MySwitchButton extends View {


    private Paint mPaint;
    //背景
    private Bitmap bitmapBackGround;
    //小球
    private Bitmap bitmapBall;
    //底部
    private Bitmap bitmapBottom;
    //黑色
    private Bitmap bitmapBlack;
    //取重叠部分
    private PorterDuffXfermode pdf;
    //开关状态
    private boolean isChecked;
    //触摸X坐标
    private int touchX;
    //小球X坐标
    private int ballX = 0;
    //小球运动状态
    private int ballMoveState = LEFT_MOST;
    //图层标识
    private int saveFlags;
    //switch的宽度
    private int switchWidth;
    //switch的高度
    private int switchHeight;
    //最左边
    private static final int LEFT_MOST = 0;
    //最右边
    private static final int RIGHT_MOST = 1;
    //手指按下
    private static final int TOUCH_STATE_DOWN = 2;
    //手指移动
    private static final int TOUCH_STATE_MOVE = 3;
    //手指抬起
    private static final int TOUCH_STATE_UP = 4;

    private onSwitchListener mListener;

    public MySwitchButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.SwitchButtonView);
        isChecked = ta.getBoolean(R.styleable.SwitchButtonView_checked, false);
        ta.recycle();
        init(context);
    }

    public MySwitchButton(Context context) {
        this(context, null);
    }

    private void init(Context context) {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);

        pdf = new PorterDuffXfermode(PorterDuff.Mode.SRC_IN); //2张重叠 取上面一张重叠部分

        saveFlags = Canvas.MATRIX_SAVE_FLAG | Canvas.CLIP_SAVE_FLAG |
                Canvas.HAS_ALPHA_LAYER_SAVE_FLAG | Canvas.FULL_COLOR_LAYER_SAVE_FLAG | Canvas.CLIP_TO_LAYER_SAVE_FLAG;

        bitmapBackGround = BitmapFactory.decodeResource(getResources(), R.mipmap.switch_bg);
        bitmapBall = BitmapFactory.decodeResource(getResources(), R.mipmap.switch_ball);
        bitmapBottom = BitmapFactory.decodeResource(getResources(), R.mipmap.switch_bottom);
        bitmapBlack = BitmapFactory.decodeResource(getResources(), R.mipmap.switch_black);

        switchWidth = bitmapBackGround.getWidth();
        switchHeight = bitmapBackGround.getHeight();
        //开
        if (isChecked) {
            ballMoveState = RIGHT_MOST;
            ballX = bitmapBackGround.getWidth() - bitmapBall.getWidth();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(switchWidth, switchHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //增加图层
        canvas.saveLayer(0, 0, switchWidth, switchHeight, null, saveFlags);
        //底部是黑色图层
        canvas.drawBitmap(bitmapBlack, 0, 0, mPaint);
        mPaint.setXfermode(pdf);
        if (isChecked) {
            canvas.drawBitmap(bitmapBottom, 0 - (switchWidth - bitmapBall.getWidth() - ballX), 0, mPaint);
        } else {
            canvas.drawBitmap(bitmapBottom, -(bitmapBottom.getWidth() / 2 - bitmapBall.getWidth() / 2) + ballX, 0, mPaint);
        }
        mPaint.setXfermode(null);
        canvas.restore();
        ballMoveState(canvas);
    }

    /**
     * 滑动状态绘制
     *
     * @param canvas
     */
    private void ballMoveState(Canvas canvas) {
        switch (ballMoveState) {
            case TOUCH_STATE_DOWN:
            case TOUCH_STATE_MOVE:
                if (touchX > 0 && touchX < switchWidth - bitmapBall.getWidth()) {
                    canvas.drawBitmap(bitmapBall, touchX, 0, mPaint);
                } else if (touchX <= 0) {
                    canvas.drawBitmap(bitmapBall, 0, 0, mPaint);
                } else if (touchX >= switchWidth - bitmapBall.getWidth()) {
                    canvas.drawBitmap(bitmapBall, switchWidth - bitmapBall.getWidth(), 0, mPaint);
                }
                break;
            case TOUCH_STATE_UP:
            case LEFT_MOST:
                if (touchX > 0 && touchX < switchWidth / 2) {
                    canvas.drawBitmap(bitmapBall, 0, 0, mPaint);
                } else if (touchX >= switchWidth / 2 && touchX <= switchWidth) {
                    canvas.drawBitmap(bitmapBall, switchWidth - bitmapBall.getWidth(), 0, mPaint);
                } else if (touchX <= 0) {
                    canvas.drawBitmap(bitmapBall, 0, 0, mPaint);
                } else if (touchX >= switchWidth - bitmapBall.getWidth()) {
                    canvas.drawBitmap(bitmapBall, switchWidth - bitmapBall.getWidth(), 0, mPaint);
                }
                break;
            case RIGHT_MOST:
                canvas.drawBitmap(bitmapBall, switchWidth - bitmapBall.getWidth(), 0, mPaint);
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touchStateChange((int) event.getX(), TOUCH_STATE_DOWN);
                break;
//            case MotionEvent.ACTION_MOVE:
//                touchStateChange((int) event.getX(), TOUCH_STATE_MOVE);
//                break;
            case MotionEvent.ACTION_UP:
                touchStateChange((int) event.getX(), TOUCH_STATE_UP);
                break;
            default:
                break;
        }
        return true;
    }
    /**
     * 触摸状态改变
     *
     * @param mTouchX
     * @param touchState
     */
    private void touchStateChange(int mTouchX, int touchState) {
        ballX = touchX = mTouchX;
        if (touchX <= 0) {
            ballX = 0;
        }
        if (touchX >= switchWidth - bitmapBall.getWidth()) {
            ballX = switchWidth - bitmapBall.getWidth();
        }
        ballMoveState = touchState;
        if (ballMoveState == TOUCH_STATE_UP) { //手指抬起
            ballX = 0;
            if (touchX >= switchWidth / 2f) {
                isChecked = true;
                ballX = switchWidth - bitmapBall.getWidth();
            } else {
                isChecked = false;
            }
            if (mListener != null) {
                mListener.onSwitchChanged(isChecked);
            }
        }
        invalidate();
    }

    public void setOnSwitchListener(onSwitchListener listener) {
        this.mListener = listener;
    }

    public interface onSwitchListener {
        void onSwitchChanged(boolean isCheck);
    }



}
