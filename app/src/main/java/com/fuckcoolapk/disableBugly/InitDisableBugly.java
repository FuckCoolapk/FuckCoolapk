package com.fuckcoolapk.disableBugly;

import android.content.Context;

import com.fuckcoolapk.utils.OwnSharedPreferences;

import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

public class InitDisableBugly {
    public void init(ClassLoader classLoader) {
        if (OwnSharedPreferences.getInstance().getSharedPreferences().getBoolean("disableBugly",false)){
            XposedBridge.hookAllMethods(XposedHelpers.findClass("com.tencent.bugly.crashreport.CrashReport", classLoader), "initCrashReport", new XC_MethodReplacement() {
                @Override
                protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                    return null;
                }
            });
        }
    }
}
