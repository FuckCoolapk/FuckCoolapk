package com.fuckcoolapk.utils

import android.app.Activity
import android.content.Context
import com.fuckcoolapk.utils.ktx.callMethod
import com.fuckcoolapk.utils.ktx.callStaticMethod
import de.robv.android.xposed.XposedHelpers

object CoolapkContext {
    lateinit var context: Context
    lateinit var classLoader: ClassLoader
    lateinit var activity: Activity
    val loginSession by lazy { XposedHelpers.findClass("com.coolapk.market.manager.DataManager", classLoader).callStaticMethod("getInstance")?.callMethod("getLoginSession")!! }
}