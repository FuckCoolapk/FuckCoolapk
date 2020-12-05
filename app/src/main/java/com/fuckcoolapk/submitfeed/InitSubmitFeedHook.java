package com.fuckcoolapk.submitfeed;

import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fuckcoolapk.InitHook;
import com.fuckcoolapk.utils.AppUtil;
import com.fuckcoolapk.view.CheckBoxForHook;
import com.fuckcoolapk.view.SwitchForHook;

import java.lang.reflect.Field;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedHelpers;

public class InitSubmitFeedHook {
    public void init(ClassLoader classLoader){
        XposedHelpers.findAndHookMethod("com.coolapk.market.view.feedv8.SubmitExtraViewPart", classLoader, "initWith", "com.coolapk.market.view.feedv8.SubmitFeedV8Activity", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                LinearLayout view = (LinearLayout) XposedHelpers.callMethod(param.thisObject,"getView");
                LinearLayout linearLayout = new LinearLayout(InitHook.activity);
                linearLayout.setOrientation(LinearLayout.VERTICAL);
                CheckBoxForHook checkBoxForHook = new CheckBoxForHook(InitHook.activity);
                LinearLayout.LayoutParams lp =new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                lp.gravity= Gravity.RIGHT;
                lp.rightMargin=AppUtil.dp2px(InitHook.activity,10f);
                checkBoxForHook.setLayoutParams(lp);
                //checkBoxForHook.setPadding(AppUtil.dp2px(InitHook.activity,10.f),0,0,0);
                checkBoxForHook.setText("临时关闭水印");
                checkBoxForHook.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        if (b){
                            Object instance = null;
                            try {
                                instance = XposedHelpers.findClass("com.coolapk.market.view.settings.settingsynch.SettingSynchronized",classLoader);
                                XposedHelpers.callMethod(instance,"uploadSetting","picture_watermark_position",0);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            //SettingSynchronized.uploadSetting(classLoader,"picture_watermark_position",0);
                        }else {
                            SettingSynchronized.uploadSetting(classLoader,"picture_watermark_position",0);
                        }
                    }
                });
                linearLayout.addView(checkBoxForHook);
                view.addView(linearLayout);
                super.afterHookedMethod(param);
            }
        });
    }
}
