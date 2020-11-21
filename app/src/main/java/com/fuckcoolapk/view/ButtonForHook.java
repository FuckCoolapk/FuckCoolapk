package com.fuckcoolapk.view;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.util.AttributeSet;
import android.widget.Button;

import com.fuckcoolapk.R;

public class ButtonForHook extends Button {
    public ButtonForHook(Context context) {
        super(context);
    }

    public ButtonForHook(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ButtonForHook(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public ButtonForHook(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }
    {
        //this.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ff109d58")));
        //this.setBackgroundColor(Color.RED);
    }
}
