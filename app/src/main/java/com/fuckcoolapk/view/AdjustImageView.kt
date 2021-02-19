package com.fuckcoolapk.view

import android.content.Context
import android.util.AttributeSet
import android.widget.ImageView

open class AdjustImageView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : ImageView(context, attrs, defStyleAttr) {
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        drawable?.let {
            val width = MeasureSpec.getSize(widthMeasureSpec)
            val height = Math.ceil(width.toDouble() * it.intrinsicHeight / it.intrinsicWidth).toInt()
            setMeasuredDimension(width, height)
        } ?: super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }
    init {
        adjustViewBounds=true
        scaleType=ScaleType.FIT_XY
    }
}