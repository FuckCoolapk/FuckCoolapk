package com.fuckcoolapk;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class MainActivity extends Activity {
    private Switch removeStartupAdsSwitch;
    private Switch goToAppTabByDefaultSwitch;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //
        removeStartupAdsSwitch = findViewById(R.id.removeStartupAdsSwitch);
        goToAppTabByDefaultSwitch = findViewById(R.id.goToAppTabByDefaultSwitch);
        //
        SharedPreferences.Editor editor = getSharedPreferences("fuckcoolapk",MODE_WORLD_READABLE).edit();
        editor.putString("package_name","");
        /*editor.putString("block_list","\ncom.coolapk.market@@@-1~~~3|RelativeLayout/1|LinearLayout/0|FrameLayout/2|FrameLayout/0|LinearLayout/0|SearchAppHeader/1|CoordinatorLayout/0|DrawSystemBarFrameLayout/0|FrameLayout/1|ContentFrameLayout/0|FitWindowsLinearLayout/1|FrameLayout/0|LinearLayout/#~~~648,44$$@@@99+@@@RelativeLayout@@@true\n" +
                "com.coolapk.market@@@2131362113~~~0|FrameLayout/1|LinearLayout/2|AHBottomNavigation/1|CoordinatorLayout/0|DrawSystemBarFrameLayout/0|FrameLayout/1|ContentFrameLayout/0|FitWindowsLinearLayout/1|FrameLayout/0|LinearLayout/#~~~0,1208$$@@@首页@@@FrameLayout@@@true\n" +
                "com.coolapk.market@@@2131362113~~~1|FrameLayout/1|LinearLayout/2|AHBottomNavigation/1|CoordinatorLayout/0|DrawSystemBarFrameLayout/0|FrameLayout/1|ContentFrameLayout/0|FitWindowsLinearLayout/1|FrameLayout/0|LinearLayout/#~~~72,1208$$@@@数码@@@FrameLayout@@@true\n" +
                "com.coolapk.market@@@2131362992~~~3|PostButtonView2/1|CoordinatorLayout/0|DrawSystemBarFrameLayout/0|FrameLayout/1|ContentFrameLayout/0|FitWindowsLinearLayout/1|FrameLayout/0|LinearLayout/#~~~3");
        editor.apply();*/
        writeStringToFile("\ncom.coolapk.market@@@-1~~~3|RelativeLayout/1|LinearLayout/0|FrameLayout/2|FrameLayout/0|LinearLayout/0|SearchAppHeader/1|CoordinatorLayout/0|DrawSystemBarFrameLayout/0|FrameLayout/1|ContentFrameLayout/0|FitWindowsLinearLayout/1|FrameLayout/0|LinearLayout/#~~~648,44$$@@@99+@@@RelativeLayout@@@true\n" +
                "com.coolapk.market@@@2131362113~~~0|FrameLayout/1|LinearLayout/2|AHBottomNavigation/1|CoordinatorLayout/0|DrawSystemBarFrameLayout/0|FrameLayout/1|ContentFrameLayout/0|FitWindowsLinearLayout/1|FrameLayout/0|LinearLayout/#~~~0,1208$$@@@首页@@@FrameLayout@@@true\n" +
                "com.coolapk.market@@@2131362113~~~1|FrameLayout/1|LinearLayout/2|AHBottomNavigation/1|CoordinatorLayout/0|DrawSystemBarFrameLayout/0|FrameLayout/1|ContentFrameLayout/0|FitWindowsLinearLayout/1|FrameLayout/0|LinearLayout/#~~~72,1208$$@@@数码@@@FrameLayout@@@true\n" +
                "com.coolapk.market@@@2131362992~~~3|PostButtonView2/1|CoordinatorLayout/0|DrawSystemBarFrameLayout/0|FrameLayout/1|ContentFrameLayout/0|FitWindowsLinearLayout/1|FrameLayout/0|LinearLayout/#~~~3",Environment.getExternalStorageDirectory().toString()+"/Android/data/com.fuckcoolapk/files/","blockList.txt");
        removeStartupAdsSwitch.setChecked(Boolean.valueOf(readStringFromFile(Environment.getExternalStorageDirectory().toString()+"/Android/data/com.fuckcoolapk/files/removeStartupAds.txt")));
        goToAppTabByDefaultSwitch.setChecked(Boolean.valueOf(readStringFromFile(Environment.getExternalStorageDirectory().toString()+"/Android/data/com.fuckcoolapk/files/goToAppTabByDefault.txt")));
        //Toast.makeText(this,readStringFromFile(Environment.getExternalStorageDirectory().toString()+"/Android/data/"+getApplication().getPackageName()+"/files/removeStartupAds.txt"),Toast.LENGTH_SHORT).show();
        //
        removeStartupAdsSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    writeStringToFile("true",Environment.getExternalStorageDirectory().toString()+"/Android/data/com.fuckcoolapk/files/","removeStartupAds.txt");
                }else {
                    writeStringToFile("false",Environment.getExternalStorageDirectory().toString()+"/Android/data/com.fuckcoolapk/files/","removeStartupAds.txt");
                }
            }
        });
        goToAppTabByDefaultSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    writeStringToFile("true",Environment.getExternalStorageDirectory().toString()+"/Android/data/com.fuckcoolapk/files/","goToAppTabByDefault.txt");
                }else {
                    writeStringToFile("false",Environment.getExternalStorageDirectory().toString()+"/Android/data/com.fuckcoolapk/files/","goToAppTabByDefault.txt");
                }
            }
        });
    }
    //写数据到文件
    private static void writeStringToFile(String string,String path,String fileName){
        try {
            File file = new File(path);
            if (!file.isDirectory()) {
                file.mkdirs();
            }
            FileOutputStream out = new FileOutputStream(path+fileName);
            byte[] b = string.getBytes();
            for (int i = 0; i < b.length; i++) {
                out.write(b[i]);
            }
            out.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    //读数据
    private String readStringFromFile (String path) {
        File file = new File(path);
        Long filelength = file.length(); // 获取文件长度
        byte[] filecontent = new byte[filelength.intValue()];
        try
        {
            FileInputStream in = new FileInputStream(file);
            in.read(filecontent);
            in.close();
        } catch (FileNotFoundException e)
        {
            e.printStackTrace();
        } catch (IOException e)
        {
            e.printStackTrace();
        }

        String[] fileContentArr = new String(filecontent).split("\r\n");

        return fileContentArr[0];// 返回文件内容,默认编码
    }
}
