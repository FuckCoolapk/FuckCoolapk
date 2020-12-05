package com.fuckcoolapk.submitfeed;

import java.lang.reflect.Field;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;

public class SettingSynchronized {
    public static void uploadSetting(ClassLoader classLoader,String str,int i){
        try {
            Object instance = XposedHelpers.findClass("com.coolapk.market.view.settings.settingsynch.SettingSynchronized",classLoader).newInstance();
            XposedHelpers.callMethod(instance,"uploadSetting",str,i);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
