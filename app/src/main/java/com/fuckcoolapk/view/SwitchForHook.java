package com.fuckcoolapk.view;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.widget.Switch;

import com.fuckcoolapk.utils.AppUtil;

public class SwitchForHook extends Switch {
    public SwitchForHook(Context context) {
        super(context);
    }

    public SwitchForHook(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SwitchForHook(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public SwitchForHook(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    {
        this.setTextColor(Color.BLACK);
        this.setPadding(AppUtil.dp2px(super.getContext(),10), AppUtil.dp2px(super.getContext(),10),AppUtil.dp2px(super.getContext(),10),AppUtil.dp2px(super.getContext(),10));
    }
}
