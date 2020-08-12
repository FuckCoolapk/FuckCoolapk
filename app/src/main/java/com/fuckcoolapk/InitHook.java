package com.fuckcoolapk;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import static android.content.ContentValues.TAG;
import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;

public class InitHook implements IXposedHookLoadPackage {
    private Activity activity;
    private static boolean onlyOnce = false;

    @Override
    public void handleLoadPackage(final XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        if (lpparam.packageName.equals("com.coolapk.market")) {
            //获取Activity
            Class<?> instrumentation = XposedHelpers.findClass("android.app.Instrumentation", lpparam.classLoader);
            XposedBridge.hookAllMethods(instrumentation, "newActivity", new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    activity = (Activity) param.getResult();
                    Log.v(TAG, "Current Activity : " + activity.getClass().getName());
                }
            });
            //去除开屏广告
            if (Boolean.valueOf(readStringFromFile(Environment.getExternalStorageDirectory().toString() + "/Android/data/com.fuckcoolapk/files/removeStartupAds.txt"))) {
                try {
                    findAndHookMethod("com.coolapk.market.view.splash.SplashActivity$Companion", lpparam.classLoader, "shouldShowAd", Context.class, XC_MethodReplacement.returnConstant(false));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            //final Class clazz = XposedHelpers.findClass("com.coolapk.market.manager.ActionManager", lpparam.classLoader);
            try {
                findAndHookMethod("com.coolapk.market.view.main.MainActivity", lpparam.classLoader, "onCreate", Bundle.class, new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        //Log.d("tag", "主动调用前");
                        //XposedHelpers.callMethod(clazz.newInstance(), "startTestActivity",activity);
                        //Log.d("tag", "主动调用后");
                        //释放jniLibs
                        //FileUtil.copyFile(Environment.getExternalStorageDirectory().toString() + "/Android/data/com.fuckcoolapk/files/jniLibs/liba.so", activity.getDir("lib", Context.MODE_PRIVATE).getPath() + "/liba.so", false, false);
                        //默认转到应用页
                        SharedPreferences.Editor editor = activity.getSharedPreferences("coolapk_preferences_v7", Context.MODE_PRIVATE).edit();
                        //editor.putBoolean("feed_pic_water_mark",false);
                        if (Boolean.valueOf(readStringFromFile(Environment.getExternalStorageDirectory().toString() + "/Android/data/com.fuckcoolapk/files/goToAppTabByDefault.txt"))) {
                            editor.putString("APP_MAIN_MODE_KEY", "MARKET");
                        } else {
                            editor.putString("APP_MAIN_MODE_KEY", "SOCIAL");
                        }
                        editor.apply();
                        //第一次使用
                        SharedPreferences ownSharedPreferences = activity.getSharedPreferences("fuckcoolapk", Context.MODE_PRIVATE);
                        if (ownSharedPreferences.getBoolean("isFirstUse", true)) {
                            SharedPreferences.Editor ownEditor = ownSharedPreferences.edit();
                            ownEditor.putBoolean("isFirstUse", false);
                            ownEditor.apply();
                            final AlertDialog.Builder normalDialog = new AlertDialog.Builder(activity);
                            normalDialog.setTitle("欢迎");
                            normalDialog.setMessage("你来了？\n这是一份送给316的礼物。其功能都是默认关闭的，如需使用，请转到模块的设置页打开。");
                            normalDialog.setPositiveButton("打开",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            ComponentName componetName = new ComponentName("com.fuckcoolapk",
                                                    "com.fuckcoolapk.MainActivity");
                                            Intent intent = new Intent();
                                            intent.setComponent(componetName);
                                            activity.startActivity(intent);
                                        }
                                    });
                            normalDialog.setNegativeButton("取消",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            //...To-do
                                            dialog.dismiss();
                                        }
                                    });
                            normalDialog.setCancelable(false);
                            normalDialog.show();
                        }
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
            if (Boolean.valueOf(readStringFromFile(Environment.getExternalStorageDirectory().toString() + "/Android/data/com.fuckcoolapk/files/adminMode.txt"))) {
                try {
                    findAndHookMethod("com.coolapk.market.manager.UserPermissionChecker", lpparam.classLoader, "getCanCreateNewVote", XC_MethodReplacement.returnConstant(true));
                    findAndHookMethod("com.coolapk.market.manager.UserPermissionChecker", lpparam.classLoader, "getCanUseAdvancedVoteOptions", XC_MethodReplacement.returnConstant(true));
                    findAndHookMethod("com.coolapk.market.manager.UserPermissionChecker", lpparam.classLoader, "isLoginAdmin", XC_MethodReplacement.returnConstant(true));
                    findAndHookMethod("com.coolapk.market.local.LoginSession", lpparam.classLoader, "isAdmin", XC_MethodReplacement.returnConstant(true));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            try {
                findAndHookMethod("com.coolapk.market.AppConfig", lpparam.classLoader, "createHeaders", new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        /*ArrayList arrayList = (ArrayList) param.args[0];
                        //arrayList.remove(1);
                        //arrayList.remove(2);
                        //arrayList.remove(4);
                        //arrayList.remove();
                        ArrayList mArrayList = new ArrayList();
                        mArrayList.add(arrayList.get(0));
                        mArrayList.add(arrayList.get(0));
                        mArrayList.add(arrayList.get(0));
                        mArrayList.add(arrayList.get(3));
                        mArrayList.add(arrayList.get(4));
                        //mArrayList.add(arrayList.get(3));
                        param.args[0] = mArrayList;
                        //arrayList.clear();
                        Toast.makeText(activity,"ok",Toast.LENGTH_SHORT).show();*/
                        super.beforeHookedMethod(param);
                    }

                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        String[] result = (String[]) param.getResult();
                        //result[9] = "coolmarket";
                        //result[11] = AuthUtils.getAS(UUID.randomUUID().toString());
                        //result[11]=CoolapkAuthUtil.getAS();
                        //result[13] = "6.10.5";
                        //result[15] = "1608192";
                        //result[17] = "6";
                        //result[13]="9.6.2";
                        //result[15]="1910242";
                        //result[13]="7.9.7";
                        //result[15]="1708181";
                        //result[17]="7";
                        Log.i("X-App", result.toString());
                        //param.setResult(result);
                        super.afterHookedMethod(param);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                findAndHookMethod("com.coolapk.market.AppConfig", lpparam.classLoader, "createUserAgent", String.class, int.class, new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
                    }

                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        String result = (String) param.getResult();
                        //result = result.substring(0,result.indexOf("CoolMarket/")+11)+"7.9.7";
                        Log.i("createUserAgent", result);
                        //param.setResult(result);
                        super.afterHookedMethod(param);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
            /*try {
                findAndHookMethod("com.coolapk.market.view.feedv8.FeedEntranceV8Binding", lpparam.classLoader, "getClick", new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
                    }

                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        ViewGroup itemView8 = getHookView(param,"itemView8");
                        View childAt = itemView8.getChildAt(0);
                        if (childAt instanceof TextView){
                            ((TextView)childAt).setText("测试");
                        }
                        super.afterHookedMethod(param);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }*/
        }
    }

    public static <T> T getHookView(XC_MethodHook.MethodHookParam param, String name) throws NoSuchFieldException, IllegalAccessException {
        Class clazz = param.thisObject.getClass();
        // 通过反射获取控件，无论private或者public
        Field field = clazz.getDeclaredField(name);
        field.setAccessible(true);
        return (T) field.get(param.thisObject);
    }

    //写数据到文件
    private static void writeStringToFile(String string, String path, String fileName) {
        try {
            File file = new File(path);
            if (!file.isDirectory()) {
                file.mkdirs();
            }
            FileOutputStream out = new FileOutputStream(path + fileName);
            byte[] b = string.getBytes();
            for (int i = 0; i < b.length; i++) {
                out.write(b[i]);
            }
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //读数据
    private static String readStringFromFile(String path) {
        File file = new File(path);
        Long filelength = file.length(); // 获取文件长度
        byte[] filecontent = new byte[filelength.intValue()];
        try {
            FileInputStream in = new FileInputStream(file);
            in.read(filecontent);
            in.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String[] fileContentArr = new String(filecontent).split("\r\n");

        return fileContentArr[0];// 返回文件内容,默认编码
    }
}
