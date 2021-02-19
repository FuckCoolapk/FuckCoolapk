package com.fuckcoolapk.view;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.widget.EditText;

import com.fuckcoolapk.utils.AppUtilKt;

public class EditTextForHook extends EditText {

    {
        this.setPadding(AppUtilKt.dp2px(super.getContext(), 10), AppUtilKt.dp2px(super.getContext(), 10), AppUtilKt.dp2px(super.getContext(), 10), AppUtilKt.dp2px(super.getContext(), 10));
    }

    public EditTextForHook(Context context) {
        super(context);
    }

    public EditTextForHook(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public EditTextForHook(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public EditTextForHook(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public EditTextForHook(Context context, String hint, Boolean selectAllOnFocus, SharedPreferences sharedPreferences, String key, String defaultText) {
        super(context);
        init(hint, selectAllOnFocus, sharedPreferences, key, defaultText);
    }

    private void init(String hint, Boolean selectAllOnFocus, SharedPreferences sharedPreferences, String key, String defaultText) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        setTextSize(TextViewForHook.textSize);
        setHint(hint);
        setSelectAllOnFocus(selectAllOnFocus);
        setText(sharedPreferences.getString(key, defaultText));
        addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().equals("")){
                    editor.remove(key);
                }else {
                    editor.putString(key, s.toString());
                }
                editor.apply();
            }
        });
    }
}
