package com.coolapk.market.util;

import android.content.Context;
import android.util.Log;

import java.io.File;

public class AuthUtils {
    public static native String getAS(String str);
    static {
        //System.loadLibrary("a");
        System.load("/data/data/com.coolapk.market/app_lib/liba.so");
    }
}
