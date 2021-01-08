package com.fuckcoolapk.disableUmeng;

import android.content.Context;

import com.fuckcoolapk.utils.OwnSharedPreferences;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

public class InitDisableUmeng {
    public void init(ClassLoader classLoader){
        if (OwnSharedPreferences.getInstance().getSharedPreferences().getBoolean("disableUmeng",false)){
            XposedHelpers.findAndHookMethod("com.umeng.commonsdk.UMConfigure", classLoader, "init", Context.class, String.class, String.class, int.class, String.class, new XC_MethodReplacement() {
                @Override
                protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                    return null;
                }
            });
        }
    }
}
