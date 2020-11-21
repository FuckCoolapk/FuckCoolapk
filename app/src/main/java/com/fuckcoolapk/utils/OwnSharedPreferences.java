package com.fuckcoolapk.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class OwnSharedPreferences {
    private static OwnSharedPreferences instance;
    private Context context;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private OwnSharedPreferences(){}
    private OwnSharedPreferences(Context context){
        this.context = context;
        sharedPreferences = context.getSharedPreferences("fuckcoolapk",Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }
    public synchronized static OwnSharedPreferences getInstance(Context context){
        if (instance==null) instance=new OwnSharedPreferences(context);
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
