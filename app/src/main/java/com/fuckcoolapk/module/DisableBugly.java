package com.fuckcoolapk.module;

import android.content.Context;

import com.fuckcoolapk.utils.CoolapkContext;
import com.fuckcoolapk.utils.Log;
import com.fuckcoolapk.utils.OwnSP;

import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

public class DisableBugly {
    public void init() {
        if (OwnSP.INSTANCE.getOwnSP().getBoolean("disableBugly",false)){
            try {
                XposedBridge.hookAllMethods(XposedHelpers.findClass("com.tencent.bugly.crashreport.CrashReport", CoolapkContext.classLoader), "initCrashReport", new XC_MethodReplacement() {
                    @Override
                    protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                        return null;
                    }
                });
            }catch (Throwable e){
                Log.e(e);
            }
        }
    }
}
