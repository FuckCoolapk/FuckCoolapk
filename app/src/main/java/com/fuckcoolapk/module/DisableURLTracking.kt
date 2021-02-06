package com.fuckcoolapk.module

import android.content.Context
import com.fuckcoolapk.utils.CoolapkContext
import com.fuckcoolapk.utils.LogUtil
import com.fuckcoolapk.utils.OwnSP
import com.fuckcoolapk.utils.ktx.hookBeforeMethod
import de.robv.android.xposed.XposedHelpers
import java.net.URLDecoder
import java.net.URLEncoder

class DisableURLTracking {
    fun init(){
        if (OwnSP.ownSP.getBoolean("disableURLTracking",false)){
            XposedHelpers.findClass("com.coolapk.market.manager.ActionManager",CoolapkContext.classLoader)
                    .hookBeforeMethod("startBrowserActivity",Context::class.java, String::class.java){
                        val url = it.args[1] as String
                        it.args[1]=URLDecoder.decode(url.substring(url.indexOf("https://www.coolapk.com/link")+33),"utf-8")
                    }
        }
    }
}