package com.fuckcoolapk.settings

import android.content.Context
import com.fuckcoolapk.utils.ktx.callMethod
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers

class InitSettingsHook2 {
    lateinit var context: Context
    var isOpen = false
    fun init(context: Context, classLoader: ClassLoader) {
        this.context = context
        XposedHelpers.findAndHookMethod("com.coolapk.market.view.settings.VXSettingsFragment", classLoader, "initData", object : XC_MethodHook() {
            override fun beforeHookedMethod(param: MethodHookParam?) {
                val fuckcoolapkHolderItem = XposedHelpers.callStaticMethod(XposedHelpers.findClass("com.coolapk.market.model.HolderItem", classLoader), "newBuilder").callMethod("entityType","holder_item")
                val list= XposedHelpers.callMethod(param?.thisObject, "getDataList") as java.util.List<*>
                //fuckcoolapkHolderItem.callMethod("entityType", "holder_item")
                list.add(fuckcoolapkHolderItem?.callMethod("string","Fuck CoolApk")?.callMethod("intValue",233)?.callMethod("build") as Nothing?)
                super.beforeHookedMethod(param)
            }
        })
    }
}
