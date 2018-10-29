package com.zzu.zk.zhiwen.customed_ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class MyCircleView extends View {

    private int viewWidth;
    private int bitmapWidth;
    private BitmapShader bitmapShader;
    private Paint paint;
    private Matrix matrix;
    private int imgSrc;
    private Bitmap bitmap;
    private float scaleRatio;
    private int radius;
    private float leftPadding;
    private float topPadding;
    private Bitmap bitmapPad;

    public MyCircleView(Context context) {
        super(context);
        getValue(context, null);
        inits();
    }


    public MyCircleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        getValue(context, attrs);
        inits();
    }

    public MyCircleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        getValue(context, attrs);
        inits();
    }

    private void getValue(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, com.example.youngkaaa.ycircleview.R.styleable.CircleView);
        imgSrc = typedArray.getResourceId(com.example.youngkaaa.ycircleview.R.styleable.CircleView_imgSrc, -1);
        leftPadding = typedArray.getDimension(com.example.youngkaaa.ycircleview.R.styleable.CircleView_leftPadding, -1);
        topPadding = typedArray.getDimension(com.example.youngkaaa.ycircleview.R.styleable.CircleView_topPadding, -1);

        Log.d("kklog", "img =====>" + imgSrc);
        Log.d("kklog", "leftPadding =====>" + leftPadding);
        Log.d("kklog", "topPadding =====>" + topPadding);
        typedArray.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = measureWidth(widthMeasureSpec);
        int height = measureHeight(heightMeasureSpec);
        viewWidth = Math.min(width, height);    //set the view's height and width were equal forced!!
        setMeasuredDimension(viewWidth, viewWidth);
        radius = viewWidth / 2;
    }

    private void inits() {
        paint = new Paint();
        matrix = new Matrix();
        paint.setAntiAlias(true);
        if (imgSrc != -1) {
            bitmap = BitmapFactory.decodeResource(getResources(), imgSrc);
        }
    }



    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
        requestLayout();
        invalidate();
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        bitmapWidth = Math.min(bitmap.getWidth(), bitmap.getHeight());
        if (bitmap.getWidth() < bitmap.getHeight() && leftPadding != -1) {
            throw new IllegalArgumentException("you can't set leftPadding when img's width<height");
        } else if (bitmap.getWidth() > bitmap.getHeight() && topPadding != -1) {
            throw new IllegalArgumentException("you can't set topPadding when img's width>height");
        }
        scaleRatio = viewWidth * 1.0f / bitmapWidth;   //计算图片缩放比例
        if (leftPadding != -1) {
            if (leftPadding + 2 * radius > bitmap.getWidth()) {
                throw new IllegalArgumentException("leftPadding is too large");
            } else {
                bitmapPad = Bitmap.createBitmap(bitmap, (int) leftPadding, 0, (int) (bitmap.getWidth() - leftPadding), (int) bitmap.getHeight());
                bitmapShader = new BitmapShader(bitmapPad, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
            }
        } else if (topPadding != -1) {
            if (topPadding + 2 * radius > bitmap.getHeight()) {
                throw new IllegalArgumentException("topPadding is too large");
            } else {
                bitmapPad = Bitmap.createBitmap(bitmap, 0, (int) topPadding, (int) bitmap.getWidth(), (int) (bitmap.getHeight() - topPadding));
                bitmapShader = new BitmapShader(bitmapPad, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
            }
        } else {
            bitmapShader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        }
        matrix.setScale(scaleRatio, scaleRatio);
        bitmapShader.setLocalMatrix(matrix);
        paint.setShader(bitmapShader);
        canvas.drawCircle(radius, radius, radius, paint);
    }

    private int measureWidth(int widthMeasureSpec) {
        int mode = MeasureSpec.getMode(widthMeasureSpec);
        int size = MeasureSpec.getSize(widthMeasureSpec);
        int result = 0;
        if (mode == MeasureSpec.EXACTLY) {
            result = size;
        } else {
            result = 1000;
            if (mode == MeasureSpec.AT_MOST) {
                result = Math.min(size, result);
            }
        }
        return result;
    }


    private int measureHeight(int heightMeasureSpec) {
        int mode = MeasureSpec.getMode(heightMeasureSpec);
        int size = MeasureSpec.getSize(heightMeasureSpec);
        int result = 0;
        if (mode == MeasureSpec.EXACTLY) {
            result = size;
        } else {
            result = 1000;
            if (mode == MeasureSpec.AT_MOST) {
                result = Math.min(size, result);
            }
        }
        return result;
    }

}
