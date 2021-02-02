package com.fuckcoolapk;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.fuckcoolapk.module.HideModule;
import com.fuckcoolapk.module.HookSettings;
import com.fuckcoolapk.submitfeed.InitSubmitFeedHook;
import com.fuckcoolapk.utils.CoolapkAuthUtilKt;
import com.sfysoft.android.xposed.shelling.XposedShelling;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Arrays;
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
import static com.fuckcoolapk.utils.FileUtil.getParamAvailability;
import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;

public class InitHook2 implements IXposedHookLoadPackage {
    private Headers appHeaders;
    public static Activity activity;
    public static Context context;
    SharedPreferences ownSharedPreferences;
    SharedPreferences.Editor ownEditor;
//    private static boolean onlyOnce = false;

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static <T> T getHookView(XC_MethodHook.MethodHookParam param, String name) throws NoSuchFieldException, IllegalAccessException {
        Class clazz = param.thisObject.getClass();
        // 通过反射获取控件，无论private或者public
        Field field = clazz.getDeclaredField(name);
        field.setAccessible(true);
        return (T) field.get(param.thisObject);
    }

    @Override
    public void handleLoadPackage(final XC_LoadPackage.LoadPackageParam lpparam) {
        if (lpparam.packageName.equals("com.coolapk.market")) {
            findAndHookMethod("com.wrapper.proxyapplication.WrapperProxyApplication", lpparam.classLoader, "attachBaseContext", Context.class, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);
                    getParamAvailability(param, Binder.getCallingPid());
                    appHeaders = new Headers.Builder()
                            .add("User-Agent", "Dalvik/2.1.0 (Linux; U; Android 5.1.1; G011A Build/LMY48Z) (#Build; google; G011A; google-user 5.1.1 20171130.276299 release-keys; 5.1.1) +CoolMarket/10.5.1-beta2-2008131")
                            .add("X-Requested-With", "XMLHttpRequest")
                            .add("X-Sdk-Int", "22")
                            .add("X-Sdk-Locale", "zh-CN")
                            .add("X-App-Id", "com.coolapk.market")
                            .add("X-App-Token", CoolapkAuthUtilKt.getAS(UUID.randomUUID().toString()))
                            .add("X-App-Version", "10.5.1-beta2")
                            .add("X-App-Code", "2008131")
                            .add("X-Api-Version", "10")
                            .add("X-App-Device", "EUMxAzRgsTZsd2bvdGI7UGbn92bnByOEBjOFVjOwMkOBFkOGdjOwYDI7YTNxEDO0AzNzgTNwYjN0AyOyczMxIzN4QzNzMDN2gDOgsjMhhTYxcTO1MWNzUTZjZGM")
                            .add("X-Dark-Mode", "0").build();
                    //获取到Context对象，通过这个对象来获取classloader
                    context = (Context) param.args[0];
                    //获取classloader，之后hook加固后的就使用这个classloader
                    ClassLoader classLoader = context.getClassLoader();
                    Log.v(AppConfig.TAG, context.getPackageName());
                    //获取Activity
                    Class<?> instrumentation = XposedHelpers.findClass("android.app.Instrumentation", classLoader);
                    XposedBridge.hookAllMethods(instrumentation, "newActivity", new XC_MethodHook() {
                        @Override
                        protected void afterHookedMethod(MethodHookParam param) {
                            activity = (Activity) param.getResult();
                            Log.v(TAG, "Current Activity : " + activity.getClass().getName());
                        }
                    });
                    //获取sp
                    //ownSharedPreferences = OwnSharedPreferences.getInstance().getSharedPreferences();
                    ownEditor = ownSharedPreferences.edit();
                    //脱壳
                    if (ownSharedPreferences.getBoolean("shouldShelling",false)){
                        if (Build.VERSION.SDK_INT<=Build.VERSION_CODES.P){
                            new XposedShelling().runShelling(lpparam);
                        }
                        ownEditor.putBoolean("shouldShelling",false).apply();
                    }
                    //关闭反xp
                    try {
                        findAndHookMethod("com.coolapk.market.util.XposedUtils", classLoader, "disableXposed", new XC_MethodReplacement() {
                            @Override
                            protected Object replaceHookedMethod(MethodHookParam param) {
                                return null;
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    //去除开屏广告
                    if (ownSharedPreferences.getBoolean("removeStartupAds", false)) {
                        try {
                            findAndHookMethod("com.coolapk.market.view.splash.FullScreenAdUtils", classLoader, "shouldShowAd", Context.class, XC_MethodReplacement.returnConstant(false));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    try {
                        findAndHookMethod("com.coolapk.market.view.main.MainActivity", classLoader, "onCreate", Bundle.class, new XC_MethodHook() {
                            @Override
                            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                                super.beforeHookedMethod(param);
                            }

                            @Override
                            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                                //第一次使用
                                if (ownSharedPreferences.getBoolean("isFirstUse", true)) {
                                    ownEditor.putBoolean("isFirstUse", false);
                                    ownEditor.apply();
                                    final AlertDialog.Builder normalDialog = new AlertDialog.Builder(activity);
                                    normalDialog.setTitle("欢迎");
                                    normalDialog.setMessage("你来了？\n这是一份送给316和423的礼物。其功能都是默认关闭的，如需使用，请转到设置页打开。");
                                    normalDialog.setPositiveButton("关闭",
                                            (dialog, which) -> dialog.dismiss());
                                    normalDialog.setCancelable(false);
                                    normalDialog.show();
                                }
                                //临时输出日志
                                if (ownSharedPreferences.getBoolean("statisticToast",false)){
                                    //CoolapkSharedPreferences.getInstance().getEditor().putBoolean("statistic_toast",true).apply();
                                }
                                super.afterHookedMethod(param);
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (ownSharedPreferences.getBoolean("checkFeedStatus", false)) {
                        try {
                            findAndHookMethod("com.coolapk.market.view.feedv8.BaseFeedContentHolder$startSubmitFeed$2", lpparam.classLoader, "onNext", "com.coolapk.market.network.Result", new XC_MethodHook() {
                                @Override
                                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                                    //Class toastClazz = XposedHelpers.findClass("com.coolapk.market.widget.Toast",lpparam.classLoader);
                                    //Object toastObject = toastClazz.newInstance();
                                    Object feed = XposedHelpers.callMethod(param.args[0], "getData");
                                    String feedID = (String) XposedHelpers.callMethod(feed, "getEntityId");
                                    //String uri = (String) XposedHelpers.callMethod(feed,"getShareUrl");
                                    new Thread(() -> {
                                        try {
                                            Thread.sleep(1000);
                                            Request request = new Request.Builder()
                                                    .url("https://api.coolapk.com/v6/feed/detail?id=" + feedID)
                                                    .headers(appHeaders).build();
                                            OkHttpClient client = new OkHttpClient();
                                            client.newCall(request).enqueue(new Callback() {
                                                @Override
                                                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                                                    Looper.prepare();
                                                    Toast.makeText(activity, "获取动态状态失败", Toast.LENGTH_SHORT).show();
                                                    Looper.loop();
                                                }

                                                @Override
                                                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                                                    try {
                                                        JSONObject jsonObject = new JSONObject(response.body().string());
                                                        String message = jsonObject.optString("message");
                                                        if (message.equals("该动态存在安全风险，暂时无法访问") | message.equals("你无法查看该内容") | message.equals("你查看的内容已被屏蔽") | message.equals("你查看的内容不存在或已被删除")) {
                                                            int foldedCount = ownSharedPreferences.getInt("foldedCount", 0);
                                                            ownEditor.putInt("foldedCount", foldedCount + 1);
                                                            ownEditor.apply();
                                                            Looper.prepare();
                                                            Toast.makeText(activity, ("这是你的动态第 %s 次被折叠\n在酷安，发现被折叠的乐趣\n（动态 ID：" + feedID + "）").replace("%s", String.valueOf(foldedCount + 1)), Toast.LENGTH_SHORT).show();
                                                        } else {
                                                            Looper.prepare();
                                                            Toast.makeText(activity, "动态状态正常", Toast.LENGTH_SHORT).show();
                                                        }
                                                        Looper.loop();
                                                    } catch (JSONException e) {
                                                        Looper.prepare();
                                                        Toast.makeText(activity, "动态状态正常", Toast.LENGTH_SHORT).show();
                                                        Looper.loop();
                                                    }
                                                }
                                            });
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                    }).start();
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
                    new HideModule().init();
                    new HookSettings().init();
                    new InitSubmitFeedHook().init(classLoader);
                    //new DisableUmeng().init(classLoader);
                    //new DisableBugly().init(classLoader);
                    //管理员模式
                    if (ownSharedPreferences.getBoolean("adminMode", false)) {
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
                                Log.i("X-App", Arrays.toString(result));
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

                }
            });

        }
    }
}
