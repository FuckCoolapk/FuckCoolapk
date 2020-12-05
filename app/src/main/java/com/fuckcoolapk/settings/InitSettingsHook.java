package com.fuckcoolapk.settings;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.fuckcoolapk.BuildConfig;
import com.fuckcoolapk.InitHook;
import com.fuckcoolapk.utils.AppUtil;
import com.fuckcoolapk.utils.OwnSharedPreferences;
import com.fuckcoolapk.view.SwitchForHook;

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
        SharedPreferences coolapkSharedPreferences = InitHook.activity.getSharedPreferences("coolapk_preferences_v7",Context.MODE_PRIVATE);
        SharedPreferences.Editor coolapkEditor = coolapkSharedPreferences.edit();
        SharedPreferences sharedPreferences = OwnSharedPreferences.getInstance(context).getSharedPreferences();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        final AlertDialog.Builder normalDialog = new AlertDialog.Builder(InitHook.activity);
        normalDialog.setTitle("Fuck CoolApk");
        ScrollView scrollView = new ScrollView(InitHook.activity);
        scrollView.setOverScrollMode(2);
        LinearLayout linearLayout = new LinearLayout(InitHook.activity);
        scrollView.addView(linearLayout);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setPadding(AppUtil.dp2px(context, 20), AppUtil.dp2px(context, 10), AppUtil.dp2px(context, 20), AppUtil.dp2px(context, 5));
        linearLayout.addView(new SwitchForHook(InitHook.activity,"去除启动广告","removeStartupAds",false));
        SwitchForHook checkFeedStatusSwitch = new SwitchForHook(InitHook.activity);
        checkFeedStatusSwitch.setText("检查动态状态");
        checkFeedStatusSwitch.setChecked(sharedPreferences.getBoolean("checkFeedStatus", false));
        checkFeedStatusSwitch.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                editor.putBoolean("checkFeedStatus", true);
                Toast.makeText(InitHook.activity,"被折叠次数在本地计算，卸载酷安后会重置。",Toast.LENGTH_SHORT).show();
            } else {
                editor.putBoolean("checkFeedStatus", false);
            }
            editor.apply();
        });
        linearLayout.addView(checkFeedStatusSwitch);
        if (BuildConfig.BUILD_TYPE.equals("debug")) {
            linearLayout.addView(new SwitchForHook(InitHook.activity,"管理员模式","adminMode",false));
        }
        SwitchForHook statisticToastSwitch = new SwitchForHook(InitHook.activity);
        statisticToastSwitch.setText("临时输出统计内容");
        statisticToastSwitch.setChecked(coolapkSharedPreferences.getBoolean("statistic_toast",false));
        statisticToastSwitch.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b){
                coolapkEditor.putBoolean("statistic_toast",true);
            }else {
                coolapkEditor.putBoolean("statistic_toast",false);
            }
            coolapkEditor.apply();
        });
        linearLayout.addView(statisticToastSwitch);
        normalDialog.setView(scrollView);
        normalDialog.setPositiveButton("重启应用",
                (dialog, which) -> {
                    System.exit(0);
                });
        normalDialog.setNegativeButton("GitHub",
                (dialog, which) -> {
                    InitHook.activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/lz233/FuckCoolapk")));
                });
        normalDialog.setNeutralButton("FAQ", (dialogInterface, i) -> {
            InitHook.activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/lz233/FuckCoolapk/wiki/FAQ")));
        });
        AlertDialog alertDialog = normalDialog.show();
        alertDialog.setOnDismissListener(dialogInterface -> isOpen = false);
        alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(Color.parseColor("#ff109d58"));
        alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(Color.parseColor("#ff109d58"));
        alertDialog.getButton(DialogInterface.BUTTON_NEUTRAL).setTextColor(Color.parseColor("#ff109d58"));
    }
}
