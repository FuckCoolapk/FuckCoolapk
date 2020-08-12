package com.fuckcoolapk;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Environment;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends Activity {
    private Switch removeStartupAdsSwitch;
    private Switch adminModeSwitch;
    private Switch goToAppTabByDefaultSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //int mode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        //setMiuiTheme(MainActivity.this,0,mode);
        setContentView(R.layout.activity_main);
        //
        removeStartupAdsSwitch = findViewById(R.id.removeStartupAdsSwitch);
        adminModeSwitch = findViewById(R.id.adminModeSwitch);
        goToAppTabByDefaultSwitch = findViewById(R.id.goToAppTabByDefaultSwitch);
        //
        SharedPreferences.Editor editor = getSharedPreferences("fuckcoolapk", MODE_WORLD_READABLE).edit();
        removeStartupAdsSwitch.setChecked(Boolean.valueOf(readStringFromFile(Environment.getExternalStorageDirectory().toString() + "/Android/data/com.fuckcoolapk/files/removeStartupAds.txt")));
        adminModeSwitch.setChecked(Boolean.valueOf(readStringFromFile(Environment.getExternalStorageDirectory().toString() + "/Android/data/com.fuckcoolapk/files/adminMode.txt")));
        goToAppTabByDefaultSwitch.setChecked(Boolean.valueOf(readStringFromFile(Environment.getExternalStorageDirectory().toString() + "/Android/data/com.fuckcoolapk/files/goToAppTabByDefault.txt")));
        if (SystemPropertyUtil.getSystemProperty("ro.product.cpu.abi").contains("x86")){
            FileUtil.copyAssets(MainActivity.this, "x86", Environment.getExternalStorageDirectory().toString() + "/Android/data/com.fuckcoolapk/files/jniLibs");
        }else {
            FileUtil.copyAssets(MainActivity.this, "armeabi", Environment.getExternalStorageDirectory().toString() + "/Android/data/com.fuckcoolapk/files/jniLibs");
        }
        //Toast.makeText(this,readStringFromFile(Environment.getExternalStorageDirectory().toString()+"/Android/data/"+getApplication().getPackageName()+"/files/removeStartupAds.txt"),Toast.LENGTH_SHORT).show();
        //
        removeStartupAdsSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    writeStringToFile("true", Environment.getExternalStorageDirectory().toString() + "/Android/data/com.fuckcoolapk/files/", "removeStartupAds.txt");
                } else {
                    writeStringToFile("false", Environment.getExternalStorageDirectory().toString() + "/Android/data/com.fuckcoolapk/files/", "removeStartupAds.txt");
                }
            }
        });
        adminModeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    writeStringToFile("true", Environment.getExternalStorageDirectory().toString() + "/Android/data/com.fuckcoolapk/files/", "adminMode.txt");
                    Toast.makeText(MainActivity.this,R.string.adminModeWarn,Toast.LENGTH_SHORT).show();
                } else {
                    writeStringToFile("false", Environment.getExternalStorageDirectory().toString() + "/Android/data/com.fuckcoolapk/files/", "adminMode.txt");
                }
            }
        });
        goToAppTabByDefaultSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    writeStringToFile("true", Environment.getExternalStorageDirectory().toString() + "/Android/data/com.fuckcoolapk/files/", "goToAppTabByDefault.txt");
                } else {
                    writeStringToFile("false", Environment.getExternalStorageDirectory().toString() + "/Android/data/com.fuckcoolapk/files/", "goToAppTabByDefault.txt");
                }
            }
        });
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
    private String readStringFromFile(String path) {
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
    public static void setMiuiTheme(Activity act, int overrideTheme,int isnightmode) {
        int themeResId = 0;
        try {
            themeResId = act.getResources().getIdentifier("Theme.DayNight", "style", "miui");
        } catch (Throwable t) {}
        if (themeResId == 0) themeResId = act.getResources().getIdentifier((isnightmode == Configuration.UI_MODE_NIGHT_YES) ? "Theme.Dark" : "Theme.Light", "style", "miui");
        act.setTheme(themeResId);
        act.getTheme().applyStyle(overrideTheme, true);
    }
}
