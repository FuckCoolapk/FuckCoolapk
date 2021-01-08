package com.fuckcoolapk.settings;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.fuckcoolapk.BuildConfig;
import com.fuckcoolapk.InitHook;
import com.fuckcoolapk.utils.AppUtil;
import com.fuckcoolapk.utils.OwnSharedPreferences;
import com.fuckcoolapk.view.ClickableTextViewForHook;
import com.fuckcoolapk.view.SwitchForHook;
import com.fuckcoolapk.view.TextViewForHook;

import java.util.List;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;

public class InitSettingsHook {
    private Context context;
    private Boolean isOpen = false;

    public void init(Context context, ClassLoader classLoader) {
        this.context = context;
        XposedHelpers.findAndHookMethod("com.coolapk.market.view.settings.VXSettingsFragment", classLoader, "initData", new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                Object fuckcoolapkHolderItem = XposedHelpers.callStaticMethod(XposedHelpers.findClass("com.coolapk.market.model.HolderItem", classLoader), "newBuilder");
                fuckcoolapkHolderItem = XposedHelpers.callMethod(fuckcoolapkHolderItem, "entityType", "holder_item");
                Object lineHolderItem = XposedHelpers.callMethod(fuckcoolapkHolderItem, "intValue", 14);
                lineHolderItem = XposedHelpers.callMethod(lineHolderItem, "build");
                fuckcoolapkHolderItem = XposedHelpers.callMethod(fuckcoolapkHolderItem, "string", "Fuck CoolApk");
                fuckcoolapkHolderItem = XposedHelpers.callMethod(fuckcoolapkHolderItem, "intValue", 233);
                fuckcoolapkHolderItem = XposedHelpers.callMethod(fuckcoolapkHolderItem, "build");
                List list = (List) XposedHelpers.callMethod(param.thisObject, "getDataList");
                list.add(fuckcoolapkHolderItem);
                list.add(lineHolderItem);
                //Log.v(AppConfig.TAG,fuckcoolapkHolderItem.toString());
                super.beforeHookedMethod(param);
            }
        });
        XposedHelpers.findAndHookConstructor("com.coolapk.market.view.settings.VXSettingsFragment$onCreateViewHolder$1", classLoader, "com.coolapk.market.view.settings.VXSettingsFragment", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                List list = (List) XposedHelpers.callMethod(param.args[0], "getDataList");
                XposedHelpers.findAndHookMethod("com.coolapk.market.view.settings.VXSettingsFragment$onCreateViewHolder$1", classLoader, "onItemClick", "androidx.recyclerview.widget.RecyclerView$ViewHolder", View.class, new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        Object obj = list.get((Integer) XposedHelpers.callMethod(param.args[0], "getAdapterPosition"));
                        Integer intValue = (Integer) XposedHelpers.callMethod(obj, "getIntValue");
                        //Log.v(AppConfig.TAG, intValue.toString());
                        if (intValue != null & intValue == 233 & !isOpen) {
                            showSettingsDialog();
                            isOpen = true;
                            /*Object settingActivity = XposedHelpers.callStaticMethod(XposedHelpers.findClass("com.coolapk.market.view.base.SimpleActivity",classLoader),"builder",InitHook.activity);
                            settingActivity=XposedHelpers.callMethod(settingActivity,"fragmentClass",XposedHelpers.findClass("com.coolapk.market.view.settings.VXSettingsFragment",classLoader));
                            settingActivity=XposedHelpers.callMethod(settingActivity,"title","Fuck CoolApk");
                            XposedHelpers.callMethod(settingActivity,"start");*/
                        }
                        super.beforeHookedMethod(param);
                    }
                });
                super.afterHookedMethod(param);
            }
        });
    }

    private void showSettingsDialog() {
        final AlertDialog.Builder normalDialog = new AlertDialog.Builder(InitHook.activity);
        //normalDialog.setTitle("Fuck CoolApk");
        ScrollView scrollView = new ScrollView(InitHook.activity);
        scrollView.setOverScrollMode(2);
        LinearLayout linearLayout = new LinearLayout(InitHook.activity);
        scrollView.addView(linearLayout);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setPadding(AppUtil.dp2px(context, 20), AppUtil.dp2px(context, 10), AppUtil.dp2px(context, 20), AppUtil.dp2px(context, 5));
        linearLayout.addView(new TextViewForHook(InitHook.activity, "Fuck CoolApk", TextViewForHook.titleSize, null));
        linearLayout.addView(new TextViewForHook(InitHook.activity,BuildConfig.VERSION_NAME+" "+BuildConfig.VERSION_CODE+" "+BuildConfig.BUILD_TYPE,null,null));
        linearLayout.addView(new TextViewForHook(InitHook.activity, "行为", TextViewForHook.title2Size, TextViewForHook.coolapkColor));
        linearLayout.addView(new SwitchForHook(InitHook.activity, "去除启动广告", OwnSharedPreferences.getInstance().getSharedPreferences(), "removeStartupAds", false));
        linearLayout.addView(new SwitchForHook(InitHook.activity, "检查动态状态", OwnSharedPreferences.getInstance().getSharedPreferences(), "checkFeedStatus", false, "被折叠次数在本地计算，卸载酷安后会重置。"));
        if (BuildConfig.BUILD_TYPE.equals("debug")) {
            linearLayout.addView(new SwitchForHook(InitHook.activity, "管理员模式", OwnSharedPreferences.getInstance().getSharedPreferences(), "adminMode", false));
        }
        linearLayout.addView(new SwitchForHook(InitHook.activity, "关闭 Umeng", OwnSharedPreferences.getInstance().getSharedPreferences(), "disableUmeng", false));
        linearLayout.addView(new SwitchForHook(InitHook.activity, "关闭腾讯 Bugly", OwnSharedPreferences.getInstance().getSharedPreferences(), "disableBugly", false));
        linearLayout.addView(new TextViewForHook(InitHook.activity, "调试", TextViewForHook.title2Size, TextViewForHook.coolapkColor));
        linearLayout.addView(new SwitchForHook(InitHook.activity, "临时输出统计内容", OwnSharedPreferences.getInstance().getSharedPreferences(), "statisticToast", false));
        linearLayout.addView(new SwitchForHook(InitHook.activity, "对酷安进行脱壳 (Android 9 -)", OwnSharedPreferences.getInstance().getSharedPreferences(), "shouldShelling", false,"仅适用于 Android P 以前的版本。\n重启应用后开始脱壳，文件存放在 /data/data/com.coolapk.market/fuck_coolapk_shell。"));
        linearLayout.addView(new TextViewForHook(InitHook.activity, "信息", TextViewForHook.title2Size, TextViewForHook.coolapkColor));
        linearLayout.addView(new ClickableTextViewForHook(InitHook.activity, "Xposed Module Repository", null, null, view -> InitHook.activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://repo.xposed.info/module/com.fuckcoolapk")))));
        linearLayout.addView(new ClickableTextViewForHook(InitHook.activity, "GitHub", null, null, view -> InitHook.activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/lz233/FuckCoolapk")))));
        linearLayout.addView(new ClickableTextViewForHook(InitHook.activity, "FAQ", null, null, view -> InitHook.activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/lz233/FuckCoolapk/wiki/FAQ")))));
        normalDialog.setView(scrollView);
        normalDialog.setPositiveButton("重启应用",
                (dialog, which) -> {
                    System.exit(0);
                });
        AlertDialog alertDialog = normalDialog.show();
        alertDialog.setOnDismissListener(dialogInterface -> isOpen = false);
        alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(Color.parseColor(TextViewForHook.coolapkColor));
        alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(Color.parseColor(TextViewForHook.coolapkColor));
        alertDialog.getButton(DialogInterface.BUTTON_NEUTRAL).setTextColor(Color.parseColor(TextViewForHook.coolapkColor));
    }
}
