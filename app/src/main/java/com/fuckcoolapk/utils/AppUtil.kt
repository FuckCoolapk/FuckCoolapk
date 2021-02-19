package com.fuckcoolapk.utils

import android.content.Context
import android.content.res.Configuration


fun dp2px(context: Context, dpValue: Float): Int = (dpValue * context.resources.displayMetrics.density + 0.5f).toInt()
fun sp2px(context: Context, spValue: Float): Int = (spValue * context.resources.displayMetrics.scaledDensity + 0.5f).toInt()
fun isNightMode(context: Context): Boolean = (context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
fun reverseColor(mString: String): String = "${(255 - mString.substring(0, 2).toInt(16)).toString(16)}${(255 - mString.substring(2, 4).toInt(16)).toString(16)}${(255 - mString.substring(4, 6).toInt(16)).toString(16)}"