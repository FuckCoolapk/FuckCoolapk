package com.fuckcoolapk.module

import com.fuckcoolapk.utils.CoolapkContext
import com.fuckcoolapk.utils.ktx.replaceMethod
import de.robv.android.xposed.XposedHelpers

class DisableAntiXposed {
    fun init() = XposedHelpers.findClass("com.coolapk.market.util.XposedUtils", CoolapkContext.classLoader)
            .replaceMethod("disableXposed") {
                null
            }
}