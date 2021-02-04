package com.fuckcoolapk.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.CheckBox;

public class CheckBoxForHook extends CheckBox {

    {
        //this.setButtonDrawable(null);
        // this.setCompoundDrawables(null,null,this.getContext().getDrawable(android.R.attr.listChoiceIndicatorMultiple),null);
    }

    public CheckBoxForHook(Context context) {
        super(context);
    }

    public CheckBoxForHook(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CheckBoxForHook(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public CheckBoxForHook(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }
}
