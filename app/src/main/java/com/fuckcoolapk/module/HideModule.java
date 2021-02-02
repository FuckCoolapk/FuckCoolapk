package com.fuckcoolapk.module;

import android.content.pm.PackageManager;

import com.fuckcoolapk.utils.CoolapkContext;
import com.fuckcoolapk.utils.Log;

import java.util.ArrayList;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;

public class HideModule {
    public void init(){
        try{
            XposedHelpers.findAndHookMethod("com.coolapk.market.util.LocalApkUtils", CoolapkContext.classLoader, "getAppList", PackageManager.class, boolean.class, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    ArrayList<Object> appList = (ArrayList<Object>) param.getResult();
                    ArrayList<Object> newAppList = new ArrayList<>();
                    for (int i=0;i<appList.size();i++){
                        Object object = appList.get(i);
                        if (!(XposedHelpers.callMethod(object,"getPackageName")).equals("com.fuckcoolapk")){
                            newAppList.add(object);
                        }
                    }
                    param.setResult(newAppList);
                    super.afterHookedMethod(param);
                }
            });
        }catch (Throwable e){
            Log.e(e);
        }
    }
}
