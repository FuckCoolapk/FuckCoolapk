package com.fuckcoolapk.view;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.Nullable;

public class ClickableTextViewForHook extends TextViewForHook {
    public ClickableTextViewForHook(Context context) {
        super(context);
    }

    public ClickableTextViewForHook(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ClickableTextViewForHook(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public ClickableTextViewForHook(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public ClickableTextViewForHook(Context context, String text, @Nullable Float size, @Nullable String color, OnClickListener onClickListener) {
        super(context, text, size, color);
        init(onClickListener);
    }

    private void init(OnClickListener onClickListener) {
        this.setOnClickListener(onClickListener);
    }
}
