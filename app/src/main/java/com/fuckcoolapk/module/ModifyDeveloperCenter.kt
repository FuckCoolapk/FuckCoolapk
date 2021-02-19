package com.fuckcoolapk.module

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import com.fuckcoolapk.utils.*
import com.fuckcoolapk.utils.ktx.callMethod
import com.fuckcoolapk.utils.ktx.callStaticMethod
import com.fuckcoolapk.utils.ktx.hookAfterMethod
import com.fuckcoolapk.utils.ktx.replaceMethod
import de.robv.android.xposed.XposedHelpers
import kotlin.math.absoluteValue


class ModifyDeveloperCenter {
    private val appTheme by lazy { XposedHelpers.findClass("com.coolapk.market.AppHolder", CoolapkContext.classLoader).callStaticMethod("getAppTheme")!! }
    fun init() {
        XposedHelpers.findClass("com.coolapk.market.manager.ActionManager", CoolapkContext.classLoader)
                .replaceMethod("startDeveloperAppListActivity", Context::class.java, String::class.java, String::class.java) {
                    XposedHelpers.findClass("com.coolapk.market.manager.ActionManager", CoolapkContext.classLoader).callStaticMethod("startWebViewActivity", it.args[0] as Context, "https://developer.coolapk.com/do?c=apk&m=myList")
                }
        XposedHelpers.findClass("com.coolapk.market.view.webview.WebViewActivity", CoolapkContext.classLoader)
                .hookAfterMethod("onCreate", Bundle::class.java) {
                    LogUtil.d(getColorFix(::getColorPrimary))
                    LogUtil.d(getTextColor())
                    //隐藏toolbar
                    val url = (it.thisObject as Activity).intent.getBundleExtra("extra_bundle")?.getString("external_url")!!
                    if (url.contains("developer.coolapk.com")) (it.thisObject.callMethod("getToolbar") as ViewGroup).visibility = View.GONE
                }
        XposedHelpers.findClass("com.coolapk.market.view.webview.WebViewFragment", CoolapkContext.classLoader)
                .hookAfterMethod("onPageFinished", WebView::class.java, String::class.java) {
                    val url = it.args[1] as String
                    if (url.contains("developer.coolapk.com")) (it.args[0] as WebView).apply {
                        //去除提示、增加退出按钮
                        if (!url.contains("m=edit") and !url.contains("m=aptitudeList"))loadUrl("javascript:void(function(){document.getElementsByTagName(\"main\")[0].getElementsByTagName(\"div\")[0].style[\"display\"]=\"none\";let a=document.getElementsByTagName(\"dd\")[0];let b=a.getElementsByTagName(\"a\");b[b.length-1].setAttribute(\"class\",\"mdl-navigation__link mdl-navigation__link--full-bleed-divider\");let c=document.createElement(\"a\");c.setAttribute(\"class\",\"mdl-navigation__link\");c.setAttribute(\"href\",\"javascript:window.opener=null;window.open('','_self');window.close();\");c.innerHTML=\"关闭页面\";a.appendChild(c);})()")
                        //自适应主题色
                        loadUrl("javascript:void(function(){const a=\"${getColorFix(::getColorPrimary)}\";const b=\"${getTextColor()}\";document.getElementsByClassName(\"mdl-layout__tab-bar-button\")[0].style[\"backgroundColor\"]=\"#00000000\";document.getElementsByClassName(\"mdl-layout__tab-bar-button\")[1].style[\"backgroundColor\"]=\"#00000000\";document.getElementsByClassName(\"mdl-layout__header\")[0].style[\"backgroundColor\"]=\"#\"+a;document.getElementsByClassName(\"mdl-layout__tab-bar\")[0].style[\"backgroundColor\"]=\"#00000000\";document.getElementsByClassName(\"mdl-layout__header\")[0].style[\"color\"]=\"#\"+b;let c=document.getElementsByClassName(\"mdl-layout__tab-bar\")[0].getElementsByTagName(\"a\");document.getElementsByClassName(\"mdl-layout__drawer-button\")[0].style[\"color\"]=\"#\"+b;for(let i=0;i<c.length;i++){c[i].style[\"color\"]=\"#\"+b}})()")
                    }
                }
    }
}