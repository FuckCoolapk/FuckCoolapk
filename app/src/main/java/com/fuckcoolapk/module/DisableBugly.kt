package com.fuckcoolapk.module

import com.fuckcoolapk.utils.CoolapkContext
import com.fuckcoolapk.utils.OwnSP
import com.fuckcoolapk.utils.ktx.replaceAfterAllMethods
import de.robv.android.xposed.XposedHelpers

class DisableBugly {
    fun init() {
        if (OwnSP.ownSP.getBoolean("disableBugly", false)) {
            XposedHelpers.findClass("com.tencent.bugly.crashreport.CrashReport", CoolapkContext.classLoader)
                    .replaceAfterAllMethods("initCrashReport") {
                        null
                    }
        }
    }
}