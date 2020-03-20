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
import android.util.Pair;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import static android.content.ContentValues.TAG;
import static com.fuckcoolapk.ViewUtils.getText;
import static com.fuckcoolapk.ViewUtils.getViewId;
import static com.fuckcoolapk.ViewUtils.getViewPath;
import static com.fuckcoolapk.ViewUtils.getViewPosition;
import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;

public class InitHook implements IXposedHookLoadPackage {
    private Activity activity;
    private static boolean onlyOnce = false;
    private ArrayList<BlockModel>[] mBlockList;
    private static XSharedPreferences xSP = new XSharedPreferences("com.fuckcoolapk", "fuckcoolapk");

    @Override
    public void handleLoadPackage(final XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        mBlockList = newArray(1);
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
            try {
                findAndHookMethod("com.coolapk.market.view.main.MainActivity", lpparam.classLoader, "onCreate", Bundle.class, new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        //默认转到应用页
                        SharedPreferences.Editor editor = activity.getSharedPreferences("coolapk_preferences_v7", Context.MODE_PRIVATE).edit();
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
                            ownEditor.putBoolean("isFirstUse",false);
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
            /*try{
                findAndHookMethod("com.aurelhubert.ahbottomnavigation.AHBottomNavigation", lpparam.classLoader, "addItems", List.class, new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        ArrayList arrayList = (ArrayList) param.args[0];
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
                        Toast.makeText(activity,"ok",Toast.LENGTH_SHORT).show();
                        super.beforeHookedMethod(param);
                    }

                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                    }
                });
            }catch (Exception e){
                e.printStackTrace();
            }*/
            /*mBlockList[0] = readBlockList(lpparam.packageName);
            XposedBridge.hookAllMethods(Activity.class, "onCreate", new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                    //Reload it!
                    mBlockList[0] = readBlockList(lpparam.packageName);
                }

            });
            XposedHelpers.findAndHookMethod(View.class, "setVisibility", int.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                    View v = (View) param.thisObject;
                    //Toast.makeText(activity, String.valueOf(v.getId()), Toast.LENGTH_SHORT).show();
                    if ((int) param.args[0] == View.GONE) {
                        return;
                    }
                    if (ViewBlocker.getInstance().isBlocked(mBlockList[0], v)) {
                        param.args[0] = View.GONE;
                        ViewBlocker.getInstance().block(v);
                    }
                }
            });
            XposedHelpers.findAndHookMethod(TextView.class, "setText", CharSequence.class, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);
                    TextView v = (TextView) param.thisObject;

                    if (ViewBlocker.getInstance().isBlocked(mBlockList[0], v)) {
                        ViewBlocker.getInstance().block(v);
                    }
                }
            });
            XposedHelpers.findAndHookMethod(View.class, "setLayoutParams", ViewGroup.LayoutParams.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    ViewGroup.LayoutParams layoutParams = (ViewGroup.LayoutParams) param.args[0];
                    if (layoutParams != null) {
                        if (layoutParams.height == 0 && layoutParams.width == 0) {
                            return;
                        }
                        if (ViewBlocker.getInstance().isBlocked(mBlockList[0], param.thisObject)) {
                            ViewBlocker.getInstance().block(param.thisObject);
                        }
                    }
                }
            });*/
        }
    }

    private static ArrayList<BlockModel> readBlockList(String pkgFilter) {
        ArrayList<BlockModel> list = new ArrayList<>();
        ArrayList<String> lines = readPreferenceByLine("block_list");
        for (String line : lines) {

            BlockModel model = BlockModel.fromString(line);
            if (model.record.contains("~~~")) {
                if (CoolApkHeadlineModel.isInstance(line)) {
                    model = CoolApkHeadlineModel.fromString(line);
                } else if (ViewModel.isInstance(line)) {
                    model = ViewModel.fromString(line);
                }
            }
            if (model != null && model.packageName.equals(pkgFilter)) {
                list.add(model);
            }
        }
        return list;
    }

    public static ArrayList<String> readPreferenceByLine(String filename) {
        //String data = xSP.getString(filename, "");
        String data = readStringFromFile(Environment.getExternalStorageDirectory().toString()+"/Android/data/com.fuckcoolapk/files/blockList.txt");
        ArrayList<String> arrayList = new ArrayList<>();
        for (String line : data.split("\n")) {
            if (!"".equals(line)) {
                arrayList.add(line);
            }
        }
        return arrayList;
    }

    static abstract class AbstractBlocker {


        /**
         * @param o 需要被记录的对象
         * @return 记录Model
         */
        @NonNull
        public abstract BlockModel log(Object o);

        /**
         * @param arrayList 记录列表
         * @param o         需要检查的对象
         * @return 是否需要屏蔽
         */
        protected abstract Pair<Boolean, Integer> isBlock(ArrayList<BlockModel> arrayList, Object o);

        /**
         * @param o 需要屏蔽的对象
         */
        public abstract void block(Object o);

        public final boolean isBlocked(ArrayList<BlockModel> arrayList, Object o) {
            Pair<Boolean, Integer> pair = isBlock(arrayList, o);
            if (onlyOnce && pair.second != null && pair.second >= 0) {
                BlockModel blockModel = arrayList.remove((int) pair.second);
            }
            return pair.first;
        }
    }

    public static class ViewBlocker extends AbstractBlocker {
        private static ViewBlocker instance;

        public static ViewBlocker getInstance() {
            if (instance == null) {
                instance = new ViewBlocker();
            }
            return instance;
        }

        @NonNull
        @Override
        public BlockModel log(Object o) {
            View view = (View) o;
            return new BlockModel(view.getContext().getPackageName(), view.getId() + "~~~" + getViewPath(view) + "~~~" + getViewPosition(view), getText(view), ViewUtils.getClassName(view.getClass()));
        }

        private static boolean singleStr(String str, char a) {
            final int length = str.length();
            boolean appeared = false;
            for (int i = 0; i < length; i++) {
                if (str.charAt(i) == a) {
                    if (appeared) {
                        return false;
                    } else {
                        appeared = true;
                    }
                }
            }
            return appeared;
        }

        /**
         * Be serious on time.
         * Be serious on time.
         * Be serious on time.
         * Think about it that will be invoked around 6,0000 times on every time of starting.
         */
        protected Pair<Boolean, Integer> isBlock(ArrayList<BlockModel> mBlockList, Object o) {
            final View view = (View) o;

            final String className = ViewUtils.getClassName(view.getClass());
            final int id = view.getId();
            final String ids = getViewId(view);

            CoolapkBlocker.getInstance().setList(mBlockList);

            final String strId = id + "";
            final int len = mBlockList.size();
            final String postion = getViewPosition(view);
            final String p = getViewPath(view);
            if (singleStr(p, '/')) {
                return new Pair<>(false, -1);
            }
            for (int i = 0; i < len; i++) {
                final BlockModel model = mBlockList.get(i);
                //className都不对，免谈了，直接跳过
                if (model.className.length() != 0 && model.className.charAt(0) != '*' && !model.className.equals(className)) {
                    continue;
                }
                if (model instanceof ViewModel) {
                    final String path = getViewPath(view);
                    int successTimes = 0;
                    if (path.equals(((ViewModel) model).getPath())) {
                        ++successTimes;
                    }
                    if (id != 0 && id != android.R.id.text1 && id != android.R.id.text2 && ((ViewModel) model).getId().equals(strId)) {
                        ++successTimes;
                    }
                    if (postion.equals(((ViewModel) model).getPosition())) {
                        successTimes += 2;
                    }
                    if (model.text.length() != 0 && model.text.equals(getText(view))) {
                        ++successTimes;
                    }
                    if (successTimes >= 2) {
                        return new Pair<>(true, i);
                    }
                }
            }
            return new Pair<>(false, -1);
        }

        @Override
        public void block(Object o) {
            View v = (View) o;
            try {
                final ViewGroup.LayoutParams layoutParams = v.getLayoutParams();
                if (layoutParams != null) {
                    layoutParams.height = 0;
                    layoutParams.width = 0;
                    if (layoutParams instanceof ViewGroup.MarginLayoutParams) {
                        ((ViewGroup.MarginLayoutParams) layoutParams).setMargins(0, 0, 0, 0);
                    }
                    v.setLayoutParams(layoutParams);
                }
                v.setPadding(0, 0, 0, 0);
                v.setAlpha(0f);
                v.setVisibility(View.GONE);
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }

    }

    @SafeVarargs
    private static <E> E[] newArray(int length, E... array) {
        return Arrays.copyOf(array, length);
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
