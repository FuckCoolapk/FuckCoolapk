package com.fuckcoolapk.module

import android.content.Context
import com.fuckcoolapk.utils.CoolapkContext
import com.fuckcoolapk.utils.Log
import com.fuckcoolapk.utils.OwnSP
import de.robv.android.xposed.XC_MethodReplacement
import de.robv.android.xposed.XposedHelpers

class RemoveStartupAds {
    fun init(){
        if (OwnSP.ownSP.getBoolean("removeStartupAds", false)){
            try {
                XposedHelpers.findAndHookMethod("com.coolapk.market.view.splash.FullScreenAdUtils", CoolapkContext.classLoader, "shouldShowAd", Context::class.java, XC_MethodReplacement.returnConstant(false))
            }catch (e:Throwable){
                Log.e(e)
            }
        }
    }
}