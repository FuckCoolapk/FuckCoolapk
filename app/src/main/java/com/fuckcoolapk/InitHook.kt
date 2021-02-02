package com.fuckcoolapk

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.os.Binder
import android.os.Bundle
import com.fuckcoolapk.module.*
import com.fuckcoolapk.utils.CoolapkContext
import com.fuckcoolapk.utils.FileUtil
import com.fuckcoolapk.utils.Log
import com.fuckcoolapk.utils.OwnSP
import com.fuckcoolapk.utils.ktx.findClass
import com.fuckcoolapk.utils.ktx.hookAfterAllMethods
import com.fuckcoolapk.utils.ktx.hookAfterMethod
import com.fuckcoolapk.utils.ktx.hookAllMethods
import com.sfysoft.android.xposed.shelling.XposedShelling
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage

class InitHook : IXposedHookLoadPackage {
    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam?) {
        if (lpparam?.packageName == "com.coolapk.market") {
            XposedHelpers.findClass("com.wrapper.proxyapplication.WrapperProxyApplication", lpparam.classLoader)
                    .hookAfterMethod("attachBaseContext", Context::class.java) {
                        //检查太极
                        FileUtil.getParamAvailability(it, Binder.getCallingPid())
                        //获取 context
                        CoolapkContext.context = it.args[0] as Context
                        //获取 classloader
                        CoolapkContext.classLoader = CoolapkContext.context.classLoader
                        Log.d(CoolapkContext.context.packageName)
                        //获取 activity
                        XposedHelpers.findClass("android.app.Instrumentation", CoolapkContext.classLoader)
                                .hookAfterAllMethods("newActivity") { activityParam ->
                                    CoolapkContext.activity = activityParam.result as Activity
                                    Log.d("Current activity: ${CoolapkContext.activity.javaClass}")
                                }
                        //脱壳
                        XposedShelling.runShelling(lpparam)
                        //第一次使用
                        try {
                            XposedHelpers.findClass("com.coolapk.market.view.main.MainActivity", CoolapkContext.classLoader)
                                    .hookAfterMethod("onCreate", Bundle::class.java) {
                                        if (OwnSP.ownSP.getBoolean("isFirstUse", true)) {
                                            OwnSP.set("isFirstUse", false)
                                            val normalDialog = AlertDialog.Builder(CoolapkContext.activity)
                                            normalDialog.setTitle("欢迎")
                                            normalDialog.setMessage("你来了？\n这是一份送给316和423的礼物。其功能都是默认关闭的，如需使用，请转到设置页打开。")
                                            normalDialog.show()
                                        }
                                    }
                        } catch (e: Throwable) {
                            Log.e(e)
                        }
                        //关闭反 xposed
                        DisableAntiXposed().init()
                        //隐藏模块
                        HideModule().init()
                        //hook 设置
                        HookSettings().init()
                        //去除开屏广告
                        RemoveStartupAds().init()
                        //关闭友盟
                        DisableUmeng().init()
                        //关闭 bugly
                        DisableBugly().init()
                        //开启管理员模式
                        EnableAdminMode().init()
                    }
        }
    }
}