package com.zzu.zk.zhiwen.customed_ui;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class Label extends android.support.v7.widget.AppCompatTextView {
    private OnDrawableRightListener listener;

    public Label(Context context) {
        super(context);
    }

    public Label(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public Label(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    public interface OnDrawableRightListener {
        public void onDrawableRightClick(View view);
    }

    public void setOnDrawableRightClickListener(OnDrawableRightListener listener) {
        this.listener = listener;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
                if (listener != null) {
                    Drawable drawableRight = getCompoundDrawables()[2];
                    if (drawableRight != null && event.getRawX() >= (getRight() - drawableRight.getBounds().width())) {
                        listener.onDrawableRightClick(this);
                        return true;
                    }
                }

                break;
        }
        return super.onTouchEvent(event);

    }
}
