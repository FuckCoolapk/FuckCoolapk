package com.fuckcoolapk.module

import android.content.Context
import com.fuckcoolapk.utils.CoolapkContext
import com.fuckcoolapk.utils.OwnSP
import com.fuckcoolapk.utils.ktx.replaceMethod
import de.robv.android.xposed.XposedHelpers

class DisableUmeng {
    fun init(){
        if (OwnSP.ownSP.getBoolean("disableUmeng",false)){
            XposedHelpers.findClass("com.umeng.commonsdk.UMConfigure",CoolapkContext.classLoader)
                    .replaceMethod("init",Context::class.java, String::class.java,String::class.java,Int::class.javaPrimitiveType, String::class.java){
                        null
                    }
        }
    }
}