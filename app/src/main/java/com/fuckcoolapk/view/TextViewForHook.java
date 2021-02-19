package com.fuckcoolapk.view;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.fuckcoolapk.utils.AppUtilKt;
import com.fuckcoolapk.utils.CoolapkContext;
import com.fuckcoolapk.utils.CoolapkContextKt;

public class TextViewForHook extends TextView {
    public static float titleSize = AppUtilKt.sp2px(CoolapkContext.context, 10f);
    public static float title2Size = AppUtilKt.sp2px(CoolapkContext.context, 8f);
    public static float textSize = AppUtilKt.sp2px(CoolapkContext.context, 6f);

    {
        this.setPadding(AppUtilKt.dp2px(super.getContext(), 10), AppUtilKt.dp2px(super.getContext(), 10), AppUtilKt.dp2px(super.getContext(), 10), AppUtilKt.dp2px(super.getContext(), 10));
    }

    public TextViewForHook(Context context) {
        super(context);
    }

    public TextViewForHook(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public TextViewForHook(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public TextViewForHook(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public TextViewForHook(Context context, String text, @Nullable Float size, @Nullable String color) {
        super(context);
        init(text, size, color);
    }

    private void init(String text, @Nullable Float size, @Nullable String color) {
        this.setText(text);
        if (size != null) {
            this.setTextSize(size);
        }
        if (color != null) {
            this.setTextColor(Color.parseColor(color));
        } else {
            if (AppUtilKt.isNightMode(super.getContext())) {
                this.setTextColor(Color.WHITE);
            } else {
                this.setTextColor(Color.BLACK);
            }
        }
    }
}
