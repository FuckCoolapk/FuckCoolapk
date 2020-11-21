package com.fuckcoolapk.view;

import android.content.Context;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

public class LinearLayoutForHook extends LinearLayout {
    public LinearLayoutForHook(Context context, @Nullable Integer width, @Nullable Integer height) {
        super(context);
        LinearLayout.LayoutParams linearParams = (LinearLayout.LayoutParams) this.getLayoutParams();
        if (width != null) linearParams.width = width;
        if (height != null) linearParams.height = height;
        this.setLayoutParams(linearParams);
    }
}
