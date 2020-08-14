package com.coolapk.market.util;

public class AuthUtils {
    static {
        //System.loadLibrary("a");
        System.load("/data/data/com.coolapk.market/app_lib/liba.so");
    }

    public static native String getAS(String str);
}
