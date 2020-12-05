package com.fuckcoolapk.settings;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.view.View;
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
                        }
                        super.beforeHookedMethod(param);
                    }
                });
                super.afterHookedMethod(param);
            }
        });
    }

    private void showSettingsDialog() {
        SharedPreferences sharedPreferences = OwnSharedPreferences.getInstance(context).getSharedPreferences();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        final AlertDialog.Builder normalDialog = new AlertDialog.Builder(InitHook.activity);
        normalDialog.setTitle("Fuck CoolApk");
        ScrollView scrollView = new ScrollView(InitHook.activity);
        LinearLayout linearLayout = new LinearLayout(InitHook.activity);
        scrollView.addView(linearLayout);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setPadding(AppUtil.dp2px(context, 20), AppUtil.dp2px(context, 10), AppUtil.dp2px(context, 20), AppUtil.dp2px(context, 5));
        SwitchForHook removeStartupAdsSwitch = new SwitchForHook(InitHook.activity);
        removeStartupAdsSwitch.setText("去除启动广告");
        removeStartupAdsSwitch.setChecked(sharedPreferences.getBoolean("removeStartupAds", false));
        removeStartupAdsSwitch.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                editor.putBoolean("removeStartupAds", true);
            } else {
                editor.putBoolean("removeStartupAds", false);
            }
            editor.apply();
        });
        linearLayout.addView(removeStartupAdsSwitch);
        SwitchForHook goToAppTabByDefaultSwitch = new SwitchForHook(InitHook.activity);
        goToAppTabByDefaultSwitch.setText("默认转到应用页");
        goToAppTabByDefaultSwitch.setChecked(sharedPreferences.getBoolean("goToAppTabByDefault", false));
        goToAppTabByDefaultSwitch.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                editor.putBoolean("goToAppTabByDefault", true);
            } else {
                editor.putBoolean("goToAppTabByDefault", false);
            }
            editor.apply();
        });
        linearLayout.addView(goToAppTabByDefaultSwitch);
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
            SwitchForHook adminModeSwitch = new SwitchForHook(InitHook.activity);
            adminModeSwitch.setText("管理员模式");
            adminModeSwitch.setChecked(sharedPreferences.getBoolean("adminMode", false));
            adminModeSwitch.setOnCheckedChangeListener((compoundButton, b) -> {
                if (b) {
                    editor.putBoolean("adminMode", true);
                    Toast.makeText(InitHook.activity,"建议关闭，此功能很可能导致你号没了！",Toast.LENGTH_SHORT).show();
                } else {
                    editor.putBoolean("adminMode", false);
                }
                editor.apply();
            });
            linearLayout.addView(adminModeSwitch);
        }
        normalDialog.setView(scrollView);
        normalDialog.setPositiveButton("重启应用",
                (dialog, which) -> {
                    System.exit(0);
                });
        normalDialog.setNegativeButton("GitHub",
                (dialog, which) -> {
                    InitHook.activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/lz233/FuckCoolapk")));
                });
        AlertDialog alertDialog = normalDialog.show();
        alertDialog.setOnDismissListener(dialogInterface -> isOpen = false);
        alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(Color.parseColor("#ff109d58"));
        alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(Color.parseColor("#ff109d58"));
    }
}
