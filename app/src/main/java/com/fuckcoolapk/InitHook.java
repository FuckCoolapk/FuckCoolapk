package com.fuckcoolapk;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.coolapk.market.util.AuthUtils;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.UUID;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static android.content.ContentValues.TAG;
import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;

public class InitHook implements IXposedHookLoadPackage {
    private Headers appHeaders;
    private Activity mainActivity;
    private Activity activity;
    private SharedPreferences ownSharedPreferences;
    private SharedPreferences.Editor ownSharedPreferencesEditor;
    private static boolean onlyOnce = false;

    @Override
    public void handleLoadPackage(final XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        if (lpparam.packageName.equals("com.coolapk.market")) {
            appHeaders = new Headers.Builder()
                    .add("User-Agent","Dalvik/2.1.0 (Linux; U; Android 5.1.1; G011A Build/LMY48Z) (#Build; google; G011A; google-user 5.1.1 20171130.276299 release-keys; 5.1.1) +CoolMarket/10.5.1-beta2-2008131")
                    .add("X-Requested-With","XMLHttpRequest")
                    .add("X-Sdk-Int","22")
                    .add("X-Sdk-Locale","zh-CN")
                    .add("X-App-Id","com.coolapk.market")
                    .add("X-App-Token", CoolapkAuthUtil.getAS(UUID.randomUUID().toString()))
                    .add("X-App-Version","10.5.1-beta2")
                    .add("X-App-Code","2008131")
                    .add("X-Api-Version","10")
                    .add("X-App-Device","EUMxAzRgsTZsd2bvdGI7UGbn92bnByOEBjOFVjOwMkOBFkOGdjOwYDI7YTNxEDO0AzNzgTNwYjN0AyOyczMxIzN4QzNzMDN2gDOgsjMhhTYxcTO1MWNzUTZjZGM")
                    .add("X-Dark-Mode","0").build();
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
                        ownSharedPreferences = activity.getSharedPreferences("fuckcoolapk", Context.MODE_PRIVATE);
                        ownSharedPreferencesEditor = ownSharedPreferences.edit();
                        if (ownSharedPreferences.getBoolean("isFirstUse", true)) {
                            ownSharedPreferencesEditor.putBoolean("isFirstUse", false);
                            ownSharedPreferencesEditor.apply();
                            final AlertDialog.Builder normalDialog = new AlertDialog.Builder(activity);
                            normalDialog.setTitle("欢迎");
                            normalDialog.setMessage("你来了？\n这是一份送给316的礼物。其功能都是默认关闭的，如需使用，请转到模块的设置页打开。");
                            normalDialog.setPositiveButton("打开",
                                    (dialog, which) -> {
                                        ComponentName componetName = new ComponentName("com.fuckcoolapk",
                                                "com.fuckcoolapk.MainActivity");
                                        Intent intent = new Intent();
                                        intent.setComponent(componetName);
                                        activity.startActivity(intent);
                                    });
                            normalDialog.setNegativeButton("取消",
                                    (dialog, which) -> dialog.dismiss());
                            normalDialog.setCancelable(false);
                            normalDialog.show();
                        }
                        super.beforeHookedMethod(param);
                    }

                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        mainActivity = activity;
                        super.afterHookedMethod(param);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (Boolean.valueOf(readStringFromFile(Environment.getExternalStorageDirectory().toString() + "/Android/data/com.fuckcoolapk/files/checkFeedStatus.txt"))) {
                try {
                    findAndHookMethod("com.coolapk.market.view.feedv8.BaseFeedContentHolder$startSubmitFeed$2", lpparam.classLoader, "onNext", "com.coolapk.market.network.Result", new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            //Class toastClazz = XposedHelpers.findClass("com.coolapk.market.widget.Toast",lpparam.classLoader);
                            //Object toastObject = toastClazz.newInstance();
                            Object feed = XposedHelpers.callMethod(param.args[0],"getData");
                            String feedID = (String) XposedHelpers.callMethod(feed,"getEntityId");
                            //String uri = (String) XposedHelpers.callMethod(feed,"getShareUrl");
                            new Thread(() -> {
                                try {
                                    Thread.sleep(1000);
                                    Request request = new Request.Builder()
                                            .url("https://api.coolapk.com/v6/feed/detail?id="+feedID)
                                            .headers(appHeaders).build();
                                    OkHttpClient client = new OkHttpClient();
                                    client.newCall(request).enqueue(new Callback() {
                                        @Override
                                        public void onFailure(@NotNull Call call, @NotNull IOException e) {
                                            Looper.prepare();
                                            Toast.makeText(activity,"获取动态状态失败",Toast.LENGTH_SHORT).show();
                                            Looper.loop();
                                        }

                                        @Override
                                        public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                                            try {
                                                JSONObject jsonObject = new JSONObject(response.body().string());
                                                String message = jsonObject.optString("message");
                                                if (message.equals("该动态存在安全风险，暂时无法访问")|message.equals("你无法查看该内容")|message.equals("你查看的内容已被屏蔽")){
                                                    int foldedCount = ownSharedPreferences.getInt("foldedCount",0);
                                                    ownSharedPreferencesEditor.putInt("foldedCount",foldedCount+1);
                                                    ownSharedPreferencesEditor.apply();
                                                    Looper.prepare();
                                                    Toast.makeText(activity,"这是你的动态第 %s 次被折叠\n和我们一起，发现被折叠的乐趣".replace("%s",String.valueOf(foldedCount+1)),Toast.LENGTH_SHORT).show();
                                                    Looper.loop();
                                                    //XposedHelpers.callStaticMethod(toastClazz,"show$default",activity,"动态已被折叠" ,0, false, 12, null);
                                                    //XposedHelpers.callMethod(toastObject,"show",activity,"动态已被折叠");
                                                }else {
                                                    Looper.prepare();
                                                    Toast.makeText(activity,"动态状态正常",Toast.LENGTH_SHORT).show();
                                                    Looper.loop();
                                                    //XposedHelpers.callStaticMethod(toastClazz,"show$default",activity,"动态状态正常" ,0, false, 12, null);
                                                }
                                            } catch (JSONException e) {
                                                Looper.prepare();
                                                Toast.makeText(activity,"动态状态正常",Toast.LENGTH_SHORT).show();
                                                Looper.loop();
                                                //e.printStackTrace();
                                            }
                                        }
                                    });
                                    /*new GetUtil().sendGet(uri, result -> {
                                        Log.d("onfuckcoolapk",result);
                                        if (result.contains("该动态存在安全风险，暂时无法访问")|result.contains("你无法查看该内容")|result.contains("你查看的内容已被屏蔽")){
                                            Toast.makeText(activity,"动态已被折叠",Toast.LENGTH_SHORT).show();
                                        }else {
                                            Toast.makeText(activity,"动态状态正常",Toast.LENGTH_SHORT).show();
                                        }
                                    });*/
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }).start();
                            //Log.d("BaseFeedContentHolder",uri);
                            //result.getData().getUrl();
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
            if (Boolean.valueOf(readStringFromFile(Environment.getExternalStorageDirectory().toString() + "/Android/data/com.fuckcoolapk/files/adminMode.txt"))) {
                try {
                    //Toast.makeText(activity,"当前已开启管理员模式，请珍惜你的账号。",Toast.LENGTH_SHORT).show();
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
