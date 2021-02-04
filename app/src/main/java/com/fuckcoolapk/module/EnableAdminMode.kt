package com.fuckcoolapk.module

import com.fuckcoolapk.utils.CoolapkContext
import com.fuckcoolapk.utils.LogUtil
import com.fuckcoolapk.utils.OwnSP
import de.robv.android.xposed.XC_MethodReplacement
import de.robv.android.xposed.XposedHelpers

class EnableAdminMode {
    fun init() {
        if (OwnSP.ownSP.getBoolean("adminMode", false)) {
            try {
                XposedHelpers.findAndHookMethod("com.coolapk.market.manager.UserPermissionChecker", CoolapkContext.classLoader, "getCanCreateNewVote", XC_MethodReplacement.returnConstant(true))
                XposedHelpers.findAndHookMethod("com.coolapk.market.manager.UserPermissionChecker", CoolapkContext.classLoader, "getCanUseAdvancedVoteOptions", XC_MethodReplacement.returnConstant(true))
                XposedHelpers.findAndHookMethod("com.coolapk.market.manager.UserPermissionChecker", CoolapkContext.classLoader, "isLoginAdmin", XC_MethodReplacement.returnConstant(true))
                XposedHelpers.findAndHookMethod("com.coolapk.market.local.LoginSession", CoolapkContext.classLoader, "isAdmin", XC_MethodReplacement.returnConstant(true))
            } catch (e: Throwable) {
                LogUtil.e(e)
            }
        }
    }
}