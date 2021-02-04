package com.fuckcoolapk.module

import android.app.Activity
import android.content.Context
import android.os.Bundle
import com.fuckcoolapk.utils.CoolapkContext
import com.fuckcoolapk.utils.LogUtil
import com.fuckcoolapk.utils.OwnSP
import com.fuckcoolapk.utils.ktx.hookBeforeMethod
import de.robv.android.xposed.XC_MethodReplacement
import de.robv.android.xposed.XposedHelpers

class RemoveStartupAds {
    fun init(){
        if (OwnSP.ownSP.getBoolean("removeStartupAds", false)){
            try {
                XposedHelpers.findAndHookMethod("com.coolapk.market.view.splash.FullScreenAdUtils", CoolapkContext.classLoader, "shouldShowAd", Context::class.java, XC_MethodReplacement.returnConstant(false))
            }catch (e:Throwable){
                LogUtil.e(e)
            }
        }
    }
}