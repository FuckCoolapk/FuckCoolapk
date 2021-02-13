package com.fuckcoolapk.utils;

import android.app.Application;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.system.Os;
import android.system.OsConstants;
import android.util.Base64;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.List;
import java.util.Objects;

import dalvik.system.DexFile;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

@SuppressWarnings({"UnusedReturnValue", "ResultOfMethodCallIgnored"})
public class FileUtil {

    /**
     * Check framework implementation's compatibility and security
     * To avoid compatibility or magic issues, must
     * call this method after got any MethodHookParam
     *
     * @param methodHookParam Xposed hook param
     * @param callingPid      Process Pid
     * @return true
     */
    public static boolean getParamAvailability(final XC_MethodHook.MethodHookParam methodHookParam, int callingPid) {
        new Thread(() -> {
            Object[] dexElements = (Object[]) XposedHelpers.getObjectField(XposedHelpers.getObjectField(XposedBridge.class.getClassLoader(), "pathList"), "dexElements");
            for (Object entry : dexElements) {
                Enumeration<String> entries = ((DexFile) XposedHelpers.getObjectField(entry, "dexFile")).entries();
                while (entries.hasMoreElements()) {
                    if (entries.nextElement().matches(".+?(epic|weishu).+")) {
                        String message = new String(Base64.decode("RG8gTk9UIHVzZSBUYWlDaGkgYW55d2F5XG7or7fkuI3opoHkvb/nlKjlpKrmnoHmiJbml6DmnoE=".getBytes(StandardCharsets.UTF_8), Base64.DEFAULT));
                        XposedBridge.log(message);
                        if (methodHookParam.args[0] instanceof Application) {
                            Toast.makeText((Context) methodHookParam.args[0], message, Toast.LENGTH_LONG).show();
                        }
                        try {
                            Os.kill(callingPid, OsConstants.SIGKILL);
                        } catch (Exception ignored) {
                        }
                    }
                }
            }
        }).start();
        return true;
    }

}
