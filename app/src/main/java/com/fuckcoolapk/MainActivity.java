package com.fuckcoolapk;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Switch;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import static com.fuckcoolapk.FileUtil.isExpModuleActive;

@SuppressWarnings("ResultOfMethodCallIgnored")
public class MainActivity extends Activity {
    public Switch removeStartupAdsSwitch;
    public Switch checkFeedStatusSwitch;
    public Switch adminModeSwitch;
    public Switch goToAppTabByDefaultSwitch;

    //写数据到文件
    private static void writeStringToFile(String string, String path, String fileName) {
        try {
            File file = new File(path);
            if (!file.isDirectory()) {
                file.mkdirs();
            }
            FileOutputStream out = new FileOutputStream(path + fileName);
            byte[] b = string.getBytes();
            for (byte value : b) {
                out.write(value);
            }
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setMiuiTheme(Activity act, int overrideTheme, int isnightmode) {
        int themeResId = 0;
        try {
            themeResId = act.getResources().getIdentifier("Theme.DayNight", "style", "miui");
        } catch (Throwable ignored) {
        }
        if (themeResId == 0)
            themeResId = act.getResources().getIdentifier((isnightmode == Configuration.UI_MODE_NIGHT_YES) ? "Theme.Dark" : "Theme.Light", "style", "miui");
        act.setTheme(themeResId);
        act.getTheme().applyStyle(overrideTheme, true);
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //int mode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        //setMiuiTheme(MainActivity.this,0,mode);
        setContentView(R.layout.activity_main);
        //
        removeStartupAdsSwitch = findViewById(R.id.removeStartupAdsSwitch);
        checkFeedStatusSwitch = findViewById(R.id.checkFeedStatusSwitch);
        adminModeSwitch = findViewById(R.id.adminModeSwitch);
        goToAppTabByDefaultSwitch = findViewById(R.id.goToAppTabByDefaultSwitch);
        //
        @SuppressWarnings("deprecation") @SuppressLint({"CommitPrefEdits", "WorldReadableFiles"}) SharedPreferences.Editor editor = getSharedPreferences("fuckcoolapk", MODE_WORLD_READABLE).edit();
        removeStartupAdsSwitch.setChecked(Boolean.parseBoolean(readStringFromFile(Environment.getExternalStorageDirectory().toString() + "/Android/data/com.fuckcoolapk/files/removeStartupAds.txt")));
        checkFeedStatusSwitch.setChecked(Boolean.parseBoolean(readStringFromFile(Environment.getExternalStorageDirectory().toString() + "/Android/data/com.fuckcoolapk/files/checkFeedStatus.txt")));
        if (BuildConfig.BUILD_TYPE.equals("debug")) adminModeSwitch.setVisibility(View.VISIBLE);
        adminModeSwitch.setChecked(Boolean.parseBoolean(readStringFromFile(Environment.getExternalStorageDirectory().toString() + "/Android/data/com.fuckcoolapk/files/adminMode.txt")));
        goToAppTabByDefaultSwitch.setChecked(Boolean.parseBoolean(readStringFromFile(Environment.getExternalStorageDirectory().toString() + "/Android/data/com.fuckcoolapk/files/goToAppTabByDefault.txt")));
        if (SystemPropertyUtil.getSystemProperty("ro.product.cpu.abi").contains("x86")) {
            FileUtil.copyAssets(MainActivity.this, "x86", Environment.getExternalStorageDirectory().toString() + "/Android/data/com.fuckcoolapk/files/jniLibs");
        } else {
            FileUtil.copyAssets(MainActivity.this, "armeabi", Environment.getExternalStorageDirectory().toString() + "/Android/data/com.fuckcoolapk/files/jniLibs");
        }
        //Toast.makeText(this,readStringFromFile(Environment.getExternalStorageDirectory().toString()+"/Android/data/"+getApplication().getPackageName()+"/files/removeStartupAds.txt"),Toast.LENGTH_SHORT).show();
        //
        if (isExpModuleActive(getApplicationContext())) {
            removeStartupAdsSwitch.setEnabled(false);
            checkFeedStatusSwitch.setEnabled(false);
            adminModeSwitch.setEnabled(false);
            goToAppTabByDefaultSwitch.setEnabled(false);
            writeStringToFile("false", Environment.getExternalStorageDirectory().toString() + "/Android/data/com.fuckcoolapk/files/", "removeStartupAds.txt");
            writeStringToFile("false", Environment.getExternalStorageDirectory().toString() + "/Android/data/com.fuckcoolapk/files/", "checkFeedStatus.txt");
            writeStringToFile("false", Environment.getExternalStorageDirectory().toString() + "/Android/data/com.fuckcoolapk/files/", "adminMode.txt");
            writeStringToFile("false", Environment.getExternalStorageDirectory().toString() + "/Android/data/com.fuckcoolapk/files/", "goToAppTabByDefault.txt");
        }
        removeStartupAdsSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                writeStringToFile("true", Environment.getExternalStorageDirectory().toString() + "/Android/data/com.fuckcoolapk/files/", "removeStartupAds.txt");
            } else {
                writeStringToFile("false", Environment.getExternalStorageDirectory().toString() + "/Android/data/com.fuckcoolapk/files/", "removeStartupAds.txt");
            }
        });
        checkFeedStatusSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                writeStringToFile("true", Environment.getExternalStorageDirectory().toString() + "/Android/data/com.fuckcoolapk/files/", "checkFeedStatus.txt");
                Toast.makeText(MainActivity.this, R.string.checkFeedStatusWarn, Toast.LENGTH_SHORT).show();
            } else {
                writeStringToFile("false", Environment.getExternalStorageDirectory().toString() + "/Android/data/com.fuckcoolapk/files/", "checkFeedStatus.txt");
            }
        });
        adminModeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                writeStringToFile("true", Environment.getExternalStorageDirectory().toString() + "/Android/data/com.fuckcoolapk/files/", "adminMode.txt");
                Toast.makeText(MainActivity.this, R.string.adminModeWarn, Toast.LENGTH_SHORT).show();
            } else {
                writeStringToFile("false", Environment.getExternalStorageDirectory().toString() + "/Android/data/com.fuckcoolapk/files/", "adminMode.txt");
            }
        });
        goToAppTabByDefaultSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                writeStringToFile("true", Environment.getExternalStorageDirectory().toString() + "/Android/data/com.fuckcoolapk/files/", "goToAppTabByDefault.txt");
            } else {
                writeStringToFile("false", Environment.getExternalStorageDirectory().toString() + "/Android/data/com.fuckcoolapk/files/", "goToAppTabByDefault.txt");
            }
        });
    }

    //读数据
    private String readStringFromFile(String path) {
        File file = new File(path);
        long filelength = file.length(); // 获取文件长度
        byte[] filecontent = new byte[(int) filelength];
        try {
            FileInputStream in = new FileInputStream(file);
            in.read(filecontent);
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String[] fileContentArr = new String(filecontent).split("\r\n");

        return fileContentArr[0];// 返回文件内容,默认编码
    }
}
