package com.fuckcoolapk.module;

import android.content.Context;

import com.fuckcoolapk.utils.CoolapkContext;
import com.fuckcoolapk.utils.Log;
import com.fuckcoolapk.utils.OwnSP;

import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedHelpers;

public class DisableUmeng {
    public void init() {
        if (OwnSP.INSTANCE.getOwnSP().getBoolean("disableUmeng", false)) {
            try {
                XposedHelpers.findAndHookMethod("com.umeng.commonsdk.UMConfigure", CoolapkContext.classLoader, "init", Context.class, String.class, String.class, int.class, String.class, new XC_MethodReplacement() {
                    @Override
                    protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                        return null;
                    }
                });
            } catch (Throwable e) {
                Log.e(e);
            }
        }
    }
}
