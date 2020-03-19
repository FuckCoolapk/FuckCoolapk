package com.fuckcoolapk;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import java.lang.reflect.Field;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;

public class InitHook implements IXposedHookLoadPackage {
    private Context context;

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        if (lpparam.packageName.equals("com.coolapk.market")) {
            //去除开屏广告
            try {
                findAndHookMethod("com.coolapk.market.view.splash.SplashActivity$Companion", lpparam.classLoader, "shouldShowAd", Context.class, XC_MethodReplacement.returnConstant(false));
            } catch (Exception e) {
                e.printStackTrace();
            }
            //获取Context
            try {
                findAndHookMethod("android.content.ContextWrapper", lpparam.classLoader, "getApplicationContext", new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                        if (context != null) return;
                        context = (Context) param.getResult();
                        XposedBridge.log("得到上下文");
                    }
                });
            } catch (Throwable t) {
                XposedBridge.log("获取上下文出错");
                XposedBridge.log(t);
            }
            //默认转到应用页
            try {
                findAndHookMethod("com.coolapk.market.view.main.MainActivity", lpparam.classLoader, "onCreate", Bundle.class, new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {

                        SharedPreferences.Editor editor = context.getSharedPreferences("coolapk_preferences_v7", Context.MODE_PRIVATE).edit();
                        editor.putString("APP_MAIN_MODE_KEY", "MARKET");
                        editor.apply();

                        super.beforeHookedMethod(param);
                    }

                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static <T> T getHookView(XC_MethodHook.MethodHookParam param, String name) throws NoSuchFieldException, IllegalAccessException {
        Class clazz = param.thisObject.getClass();
        // 通过反射获取控件，无论private或者public
        Field field = clazz.getDeclaredField(name);
        field.setAccessible(true);
        return (T) field.get(param.thisObject);
    }
}
