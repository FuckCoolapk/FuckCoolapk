package com.fuckcoolapk.module

import android.content.Context
import com.fuckcoolapk.utils.CoolapkContext
import com.fuckcoolapk.utils.OwnSP
import com.fuckcoolapk.utils.ktx.hookBeforeAllMethods
import com.fuckcoolapk.utils.ktx.replaceAfterAllMethods
import com.fuckcoolapk.utils.ktx.replaceMethod
import de.robv.android.xposed.XposedHelpers

class DisableUmeng {
    fun init() {
        if (OwnSP.ownSP.getBoolean("disableUmeng", false)) {
            XposedHelpers.findClass("com.umeng.commonsdk.UMConfigure", CoolapkContext.classLoader)
                    .replaceAfterAllMethods("init") {
                        null
                    }
            XposedHelpers.findClass("com.umeng.commonsdk.UMConfigure",CoolapkContext.classLoader)
                    .replaceAfterAllMethods("preInit"){
                        null
                    }
        }
    }
}