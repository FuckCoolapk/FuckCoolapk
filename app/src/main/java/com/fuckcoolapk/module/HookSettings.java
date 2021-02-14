package com.fuckcoolapk.module;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.fuckcoolapk.AppConfigKt;
import com.fuckcoolapk.BuildConfig;
import com.fuckcoolapk.InitHook;
import com.fuckcoolapk.InitHookKt;
import com.fuckcoolapk.utils.AppUtilKt;
import com.fuckcoolapk.utils.CoolapkContext;
import com.fuckcoolapk.utils.GetUtil;
import com.fuckcoolapk.utils.LogUtil;
import com.fuckcoolapk.utils.OwnSP;
import com.fuckcoolapk.view.ClickableTextViewForHook;
import com.fuckcoolapk.view.SwitchForHook;
import com.fuckcoolapk.view.TextViewForHook;

import java.util.List;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;

public class HookSettings {
    private Boolean isOpen = false;

    public void init() {
        try {
            XposedHelpers.findAndHookMethod("com.coolapk.market.view.settings.VXSettingsFragment", CoolapkContext.classLoader, "initData", new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    Object fuckcoolapkHolderItem = XposedHelpers.callStaticMethod(XposedHelpers.findClass("com.coolapk.market.model.HolderItem", CoolapkContext.classLoader), "newBuilder");
                    fuckcoolapkHolderItem = XposedHelpers.callMethod(fuckcoolapkHolderItem, "entityType", "holder_item");
                    Object lineHolderItem = XposedHelpers.callMethod(fuckcoolapkHolderItem, "intValue", 14);
                    lineHolderItem = XposedHelpers.callMethod(lineHolderItem, "build");
                    fuckcoolapkHolderItem = OwnSP.INSTANCE.getOwnSP().getBoolean("agreeEULA", false) ? XposedHelpers.callMethod(fuckcoolapkHolderItem, "string", "Fuck Coolapk") : XposedHelpers.callMethod(fuckcoolapkHolderItem, "string", "Fuck Coolapk（未激活）");
                    fuckcoolapkHolderItem = XposedHelpers.callMethod(fuckcoolapkHolderItem, "intValue", 233);
                    fuckcoolapkHolderItem = XposedHelpers.callMethod(fuckcoolapkHolderItem, "build");
                    List list = (List) XposedHelpers.callMethod(param.thisObject, "getDataList");
                    list.add(fuckcoolapkHolderItem);
                    list.add(lineHolderItem);
                    //Log.v(AppConfig.TAG,fuckcoolapkHolderItem.toString());
                    super.beforeHookedMethod(param);
                }
            });
            XposedHelpers.findAndHookConstructor("com.coolapk.market.view.settings.VXSettingsFragment$onCreateViewHolder$1", CoolapkContext.classLoader, "com.coolapk.market.view.settings.VXSettingsFragment", new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    List list = (List) XposedHelpers.callMethod(param.args[0], "getDataList");
                    XposedHelpers.findAndHookMethod("com.coolapk.market.view.settings.VXSettingsFragment$onCreateViewHolder$1", CoolapkContext.classLoader, "onItemClick", "androidx.recyclerview.widget.RecyclerView$ViewHolder", View.class, new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            Object obj = list.get((Integer) XposedHelpers.callMethod(param.args[0], "getAdapterPosition"));
                            Integer intValue = (Integer) XposedHelpers.callMethod(obj, "getIntValue");
                            //Log.v(AppConfig.TAG, intValue.toString());
                            if (intValue != null & intValue == 233 & !isOpen) {
                                if (OwnSP.INSTANCE.getOwnSP().getBoolean("agreeEULA", false)) {
                                    showSettingsDialog();
                                } else {
                                    boolean useFastgit = true;
                                    new GetUtil().sendGet(useFastgit ? "https://hub.fastgit.org/FuckCoolapk/FuckCoolapk/raw/master/EULA.md" : "https://cdn.jsdelivr.net/gh/FuckCoolapk/FuckCoolapk/EULA.md", result -> InitHookKt.showEulaDialog(CoolapkContext.activity, result));
                                }
                                isOpen = true;
                            }
                            super.beforeHookedMethod(param);
                        }
                    });
                    super.afterHookedMethod(param);
                }
            });
        } catch (Throwable e) {
            LogUtil.e(e);
        }
    }

    private void showSettingsDialog() {
        final AlertDialog.Builder normalDialog = new AlertDialog.Builder(CoolapkContext.activity);
        ScrollView scrollView = new ScrollView(CoolapkContext.activity);
        scrollView.setOverScrollMode(2);
        LinearLayout linearLayout = new LinearLayout(CoolapkContext.activity);
        scrollView.addView(linearLayout);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setPadding(AppUtilKt.dp2px(CoolapkContext.context, 20), AppUtilKt.dp2px(CoolapkContext.context, 10), AppUtilKt.dp2px(CoolapkContext.context, 20), AppUtilKt.dp2px(CoolapkContext.context, 5));
        linearLayout.addView(new TextViewForHook(CoolapkContext.activity, "Fuck Coolapk", TextViewForHook.titleSize, null));
        linearLayout.addView(new TextViewForHook(CoolapkContext.activity, BuildConfig.VERSION_NAME + " " + BuildConfig.VERSION_CODE + " " + BuildConfig.BUILD_TYPE + "\nTarget Version: " + AppConfigKt.MODULE_TARGET_VERSION, null, null));
        linearLayout.addView(new TextViewForHook(CoolapkContext.activity, "功能", TextViewForHook.title2Size, TextViewForHook.coolapkColor));
        linearLayout.addView(new SwitchForHook(CoolapkContext.activity, "去除启动广告", OwnSP.INSTANCE.getOwnSP(), "removeStartupAds", false));
        linearLayout.addView(new SwitchForHook(CoolapkContext.activity, "去除动态审核水印", OwnSP.INSTANCE.getOwnSP(), "removeAuditWatermark", false));
        //linearLayout.addView(new SwitchForHook(CoolapkContext.activity, "开启频道自由编辑", OwnSP.INSTANCE.getOwnSP(), "enableChannelEdit", false));
        linearLayout.addView(new SwitchForHook(CoolapkContext.activity, "对动态开启 Markdown（Alpha）", OwnSP.INSTANCE.getOwnSP(), "enableMarkdown", false));
        linearLayout.addView(new SwitchForHook(CoolapkContext.activity, "对私信开启反和谐", OwnSP.INSTANCE.getOwnSP(), "antiMessageCensorship", false, "通过自动替换相似字来达到反和谐的效果，不能保证一定有效。\n请勿滥用，请勿用于除私信外的其他地方，否则后果自负。"));
        if (BuildConfig.BUILD_TYPE.equals("debug")) {
            linearLayout.addView(new SwitchForHook(CoolapkContext.activity, "管理员模式", OwnSP.INSTANCE.getOwnSP(), "adminMode", false));
        }
        linearLayout.addView(new SwitchForHook(CoolapkContext.activity, "关闭链接追踪", OwnSP.INSTANCE.getOwnSP(), "disableURLTracking", false));
        linearLayout.addView(new SwitchForHook(CoolapkContext.activity, "关闭 Umeng", OwnSP.INSTANCE.getOwnSP(), "disableUmeng", false));
        linearLayout.addView(new SwitchForHook(CoolapkContext.activity, "关闭 Bugly", OwnSP.INSTANCE.getOwnSP(), "disableBugly", false));
        linearLayout.addView(new TextViewForHook(CoolapkContext.activity, "调试", TextViewForHook.title2Size, TextViewForHook.coolapkColor));
        //linearLayout.addView(new SwitchForHook(CoolapkContext.activity, "临时输出统计内容", OwnSP.INSTANCE.getOwnSP(), "statisticToast", false));
        linearLayout.addView(new SwitchForHook(CoolapkContext.activity, "对 酷安 进行脱壳", OwnSP.INSTANCE.getOwnSP(), "shouldShelling", false, "不适用于较新的 Android 版本。\n重启应用后开始脱壳，文件存放在 /data/data/com.coolapk.market/fuck_coolapk_shell。"));
        linearLayout.addView(new SwitchForHook(CoolapkContext.activity, "输出调试 Toast", OwnSP.INSTANCE.getOwnSP(), "showLogToast", false));
        linearLayout.addView(new TextViewForHook(CoolapkContext.activity, "信息", TextViewForHook.title2Size, TextViewForHook.coolapkColor));
        linearLayout.addView(new ClickableTextViewForHook(CoolapkContext.activity, "Xposed Module Repository", null, null, view -> CoolapkContext.activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://repo.xposed.info/module/com.fuckcoolapk")))));
        linearLayout.addView(new ClickableTextViewForHook(CoolapkContext.activity, "GitHub", null, null, view -> CoolapkContext.activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/FuckCoolapk/FuckCoolapk")))));
        linearLayout.addView(new ClickableTextViewForHook(CoolapkContext.activity, "FAQ", null, null, view -> CoolapkContext.activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/FuckCoolapk/FuckCoolapk/wiki/FAQ")))));
        normalDialog.setView(scrollView);
        normalDialog.setPositiveButton("重启应用", (dialog, which) -> System.exit(0));
        AlertDialog alertDialog = normalDialog.show();
        alertDialog.setOnDismissListener(dialogInterface -> isOpen = false);
        alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(Color.parseColor(TextViewForHook.coolapkColor));
        alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(Color.parseColor(TextViewForHook.coolapkColor));
        alertDialog.getButton(DialogInterface.BUTTON_NEUTRAL).setTextColor(Color.parseColor(TextViewForHook.coolapkColor));
    }
}
