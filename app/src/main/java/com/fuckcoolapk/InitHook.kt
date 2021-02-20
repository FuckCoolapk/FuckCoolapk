package com.fuckcoolapk

import android.app.Activity
import android.app.AlertDialog
import android.app.Application
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.os.Binder
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.ScrollView
import com.fuckcoolapk.module.*
import com.fuckcoolapk.utils.*
import com.fuckcoolapk.utils.ktx.*
import com.fuckcoolapk.view.TextViewForHook
import com.microsoft.appcenter.AppCenter
import com.microsoft.appcenter.analytics.Analytics
import com.microsoft.appcenter.crashes.Crashes
import com.sfysoft.android.xposed.shelling.XposedShelling
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage
import io.noties.markwon.Markwon
import kotlin.system.exitProcess


class InitHook : IXposedHookLoadPackage {
    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam?) {
        if (lpparam?.packageName == PACKAGE_NAME) {
            try {
                XposedHelpers.findClass("com.wrapper.proxyapplication.WrapperProxyApplication", lpparam.classLoader)
                        .hookAfterMethod("attachBaseContext", Context::class.java) {
                            //获取 context
                            CoolapkContext.context = it.args[0] as Context
                            //获取 classloader
                            CoolapkContext.classLoader = CoolapkContext.context.classLoader
                            init(lpparam, it)
                        }
            }catch (e:Throwable){
                try {
                    XposedHelpers.findAndHookMethod(Application::class.java, "attach", Context::class.java, object : XC_MethodHook() {
                        override fun afterHookedMethod(param: MethodHookParam) {
                            super.afterHookedMethod(param)
                            //获取 context
                            CoolapkContext.context = param.args[0] as Context
                            //获取 classloader
                            CoolapkContext.classLoader = CoolapkContext.context.classLoader
                            init(lpparam, param)
                        }
                    })
                } catch (e: Throwable) {
                    LogUtil.e(e)
                }
            }
        }
    }

    private fun init(lpparam: XC_LoadPackage.LoadPackageParam, param: MethodHookParam) {
        //检查太极
        FileUtil.getParamAvailability(param, Binder.getCallingPid())
        LogUtil.d(CoolapkContext.context.packageName)
        //获取 activity
        XposedHelpers.findClass("android.app.Instrumentation", CoolapkContext.classLoader)
                .hookAfterAllMethods("newActivity") { activityParam ->
                    CoolapkContext.activity = activityParam.result as Activity
                    LogUtil.d("Current activity: ${CoolapkContext.activity.javaClass}")
                }
        //eula&Appcenter
        try {
            XposedHelpers.findClass("com.coolapk.market.view.main.MainActivity", CoolapkContext.classLoader)
                    .hookAfterMethod("onCreate", Bundle::class.java) {
                        //appcenter
                        AppCenter.start(CoolapkContext.activity.application, "19597f3e-09e4-4422-9416-5dbc16cad3db", Analytics::class.java, Crashes::class.java)
                        if (CoolapkContext.loginSession.callMethod("isLogin") as Boolean) {
                            Analytics.trackEvent("user ${CoolapkContext.loginSession.callMethod("getUserName") as String}", HashMap<String, String>().apply {
                                put("userName", CoolapkContext.loginSession.callMethod("getUserName") as String)
                                put("UID", CoolapkContext.loginSession.callMethod("getUid") as String)
                                put("isAdmin", (CoolapkContext.loginSession.callMethod("isAdmin") as Boolean).toString())
                            })
                        }
                    }
        } catch (e: Throwable) {
            LogUtil.e(e)
        }
        //关闭反 xposed
        DisableAntiXposed().init()
        //隐藏模块
        HideModule().init()
        //hook 设置
        HookSettings().init()
        if (OwnSP.ownSP.getBoolean("agreeEULA", false)) {
            //脱壳
            XposedShelling.runShelling(lpparam)
            //去除开屏广告
            RemoveStartupAds().init()
            //关闭友盟
            DisableUmeng().init()
            //关闭 bugly
            DisableBugly().init()
            //开启管理员模式
            EnableAdminMode().init()
            //去除动态审核的水印
            RemoveAuditWatermark().init()
            //临时去除图片水印
            ModifyPictureWatermark().init()
            //开启频道自由编辑
            EnableChannelEdit().init()
            //对私信开启反和谐
            AntiMessageCensorship().init()
            //关闭链接追踪
            DisableURLTracking().init()
            //开启动态 Markdown
            EnableMarkdown().init()
            //点击开发者平台直接跳转网页
            ModifyDeveloperCenter().init()
        }
    }
}

fun showEulaDialog(activity: Activity, eula: String) {
    val markwon = Markwon.builder(CoolapkContext.activity).build()
    var time = 30
    val dialogBuilder = AlertDialog.Builder(activity)
    val linearLayout = LinearLayout(activity).apply {
        orientation = LinearLayout.VERTICAL
        setPadding(dp2px(CoolapkContext.context, 20f), dp2px(CoolapkContext.context, 10f), dp2px(CoolapkContext.context, 20f), dp2px(CoolapkContext.context, 5f))
        //addView(TextViewForHook(CoolapkContext.activity, "Fuck Coolapk 最终用户许可协议与隐私条款", TextViewForHook.titleSize, null))
        val message = TextViewForHook(CoolapkContext.activity, "", TextViewForHook.textSize, null)
        markwon.setMarkdown(message, eula)
        addView(message)
    }
    dialogBuilder.setView(ScrollView(CoolapkContext.activity).apply {
        overScrollMode = 2
        addView(linearLayout)
    })
    dialogBuilder.setNegativeButton("不同意") { dialogInterface: DialogInterface, i: Int ->
        LogUtil.toast("请转到 Xposed 管理器关闭此模块")
        Thread {
            Thread.sleep(1500)
            exitProcess(0)
        }.start()
    }
    dialogBuilder.setPositiveButton("我已阅读并同意本协议") { dialogInterface: DialogInterface, i: Int ->
        OwnSP.set("agreeEULA", true)
        LogUtil.toast("重新启动 酷安 后生效")
        Thread {
            Thread.sleep(1500)
            exitProcess(0)
        }.start()
    }
    dialogBuilder.setCancelable(false)
    val dialog = dialogBuilder.show()
    val negativeButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
    val positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
    when (isNightMode(CoolapkContext.context)) {
        false -> run {
            negativeButton.setTextColor(Color.BLACK)
            positiveButton.setTextColor(Color.BLACK)
        }
        true -> run {
            negativeButton.setTextColor(Color.WHITE)
            positiveButton.setTextColor(Color.WHITE)
        }
    }
    positiveButton.isClickable = false
    Thread {
        do {
            positiveButton.post { positiveButton.text = "我已阅读并同意本协议 (${time}s)" }
            Thread.sleep(1000)
        } while (--time != 0)
        positiveButton.post {
            positiveButton.text = "我已阅读并同意本协议"
            positiveButton.isClickable = true
        }
    }.start()
}