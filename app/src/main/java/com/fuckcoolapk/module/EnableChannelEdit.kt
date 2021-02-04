package com.fuckcoolapk.module

import com.fuckcoolapk.utils.CoolapkContext
import com.fuckcoolapk.utils.LogUtil
import com.fuckcoolapk.utils.OwnSP
import de.robv.android.xposed.XC_MethodReplacement
import de.robv.android.xposed.XposedHelpers

class EnableChannelEdit {
    fun init() {
        if (OwnSP.ownSP.getBoolean("enableChannelEdit", false)) {
            try {
                XposedHelpers.findAndHookMethod("com.coolapk.market.view.main.channel.Channel", CoolapkContext.classLoader, "isFixed", XC_MethodReplacement.returnConstant(false))
            } catch (e: Throwable) {
                LogUtil.e(e)
            }
        }
    }
}