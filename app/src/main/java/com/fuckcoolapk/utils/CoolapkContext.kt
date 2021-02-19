package com.fuckcoolapk.utils

import android.app.Activity
import android.content.Context
import com.fuckcoolapk.utils.ktx.callMethod
import com.fuckcoolapk.utils.ktx.callStaticMethod
import de.robv.android.xposed.XposedHelpers
import kotlin.math.absoluteValue

object CoolapkContext {
    lateinit var context: Context
    lateinit var classLoader: ClassLoader
    lateinit var activity: Activity
    val loginSession by lazy { XposedHelpers.findClass("com.coolapk.market.manager.DataManager", classLoader).callStaticMethod("getInstance")?.callMethod("getLoginSession")!! }
    val appTheme by lazy { XposedHelpers.findClass("com.coolapk.market.AppHolder", classLoader).callStaticMethod("getAppTheme")!! }
}
fun getColorFix(block:()->String):String{
    var string = block()
    while (string.length < 6) {
        string = "0$string"
    }
    return string
}
fun getColorFixWithHashtag(block: () -> String):String= "#${getColorFix(block)}"
fun getColorPrimary():String=(XposedHelpers.findClass("com.coolapk.market.util.ColorUtils", CoolapkContext.classLoader).callStaticMethod("adjustAlpha", CoolapkContext.appTheme.callMethod("getColorPrimary"), 0f) as Int).absoluteValue.toString(16)
fun getColorAccent():String=(XposedHelpers.findClass("com.coolapk.market.util.ColorUtils", CoolapkContext.classLoader).callStaticMethod("adjustAlpha", CoolapkContext.appTheme.callMethod("getColorAccent"), 0f) as Int).absoluteValue.toString(16)
fun getTextColor():String=if (CoolapkContext.appTheme.callMethod("isDayTheme") as Boolean) {
    if (CoolapkContext.appTheme.callMethod("isLightColorTheme") as Boolean) {
        "000000"
    } else {
        "ffffff"
    }
} else {
    "ffffff"
}