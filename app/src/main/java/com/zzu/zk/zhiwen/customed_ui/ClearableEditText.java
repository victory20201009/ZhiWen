package com.zzu.zk.zhiwen.customed_ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.zzu.zk.zhiwen.R;

public class ClearableEditText extends android.support.v7.widget.AppCompatEditText implements TextWatcher {
    private Drawable draw;

    public ClearableEditText(Context context) {
        this(context, null);
    }

    public ClearableEditText(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.editTextStyle);
    }

    public ClearableEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initClearDrawable();
        this.addTextChangedListener(this);
    }

    private void initClearDrawable() {
        draw = getCompoundDrawables()[2];

        // 判断清除的图片是否为空
        if (draw == null) {
            draw = getResources().getDrawable(R.drawable.ic_clear_selec);
        }

        // 将输入框默认设置为没有清除的按钮
        this.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);


    }

    @Override
    protected void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(focused, direction, previouslyFocusedRect);
        if (focused && !this.getText().toString().equals("")) {
            this.setCompoundDrawablesWithIntrinsicBounds(null, null, draw, null);
        } else {
            this.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
        }

    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence text, int start, int before, int count) {
        if (text.length() > 0) {
            this.setCompoundDrawablesWithIntrinsicBounds(null, null, draw, null);
        } else {
            this.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
        }

    }

    @Override
    public void afterTextChanged(Editable s) {

    }


    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (event.getAction() == MotionEvent.ACTION_UP) {
            // 判断所触碰的位置是否为清除的按钮
            if (event.getX() > (getWidth() - getTotalPaddingRight())
                    && event.getX() < (getWidth() - getPaddingRight())) {
                // 将editText里面的内容清除
                setText("");
            }
        }
        return super.onTouchEvent(event);

    }
}
