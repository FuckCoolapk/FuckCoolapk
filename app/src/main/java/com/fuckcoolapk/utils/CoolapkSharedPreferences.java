package com.fuckcoolapk.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.fuckcoolapk.InitHook;

public class CoolapkSharedPreferences {
    private static CoolapkSharedPreferences instance;
    private Context context;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private CoolapkSharedPreferences(){}
    private CoolapkSharedPreferences(Context context){
        this.context = context;
        sharedPreferences = context.getSharedPreferences("coolapk_preferences_v7",Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }
    public synchronized static CoolapkSharedPreferences getInstance(){
        if (instance==null) instance=new CoolapkSharedPreferences(InitHook.context);
        return instance;
    }
    public Context getContext(){
        return context;
    }
    public SharedPreferences getSharedPreferences(){
        return sharedPreferences;
    }
    public SharedPreferences.Editor getEditor(){
        return editor;
    }
}
