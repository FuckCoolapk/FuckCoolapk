package com.fuckcoolapk.view;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.util.AttributeSet;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.fuckcoolapk.utils.AppUtilKt;
import com.fuckcoolapk.utils.LogUtil;


public class SwitchForHook extends Switch {
    {
        if (AppUtilKt.isNightMode(super.getContext())) {
            this.setTextColor(Color.WHITE);
        } else {
            this.setTextColor(Color.BLACK);
        }
        //this.setButtonTintList(ColorStateList.valueOf(Color.parseColor("#ff109d58")));
        //this.setForegroundTintList(ColorStateList.valueOf(Color.parseColor("#ff109d58")));
        this.setPadding(AppUtilKt.dp2px(super.getContext(), 10), AppUtilKt.dp2px(super.getContext(), 10), AppUtilKt.dp2px(super.getContext(), 10), AppUtilKt.dp2px(super.getContext(), 10));
    }

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

    public SwitchForHook(Context context, String text, SharedPreferences sharedPreferences, String key, Boolean defaultState, @Nullable String toastText) {
        super(context);
        init(context, text, sharedPreferences, key, defaultState, toastText);
    }

    public SwitchForHook(Context context, String text, SharedPreferences sharedPreferences, String key, Boolean defaultState) {
        super(context);
        init(context, text, sharedPreferences, key, defaultState, null);
    }

    private void init(Context context, String text, SharedPreferences sharedPreferences, String key, Boolean defaultState, @Nullable String toastText) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        this.setText(text);
        this.setChecked(sharedPreferences.getBoolean(key, defaultState));
        this.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                if (toastText != null) {
                    LogUtil.INSTANCE.toast(toastText,true);
                }
                editor.putBoolean(key, true);
            } else {
                editor.putBoolean(key, false);
            }
            editor.apply();
        });
    }
}
