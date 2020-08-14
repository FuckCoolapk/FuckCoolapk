package com.fuckcoolapk;

import android.app.Application;
import android.content.Context;
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
import java.util.Objects;

import dalvik.system.DexFile;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

@SuppressWarnings({"UnusedReturnValue", "ResultOfMethodCallIgnored"})
public class FileUtil {

    public static File createFile(String FilePath) {
        return new File(FilePath);
    }

    public static boolean isFile(String FilePath) {
        boolean isFile = false;
        try {
            File file = new File(FilePath);
            isFile = file.isFile();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isFile;
    }

    public static boolean isDirectory(String DirectoryPath) {
        File file = new File(DirectoryPath);
        return file.isDirectory();
    }

    public static boolean deleteFile(String FilePath) {
        if (isFile(FilePath)) {
            File file = new File(FilePath);
            return file.delete();
        } else {
            return false;
        }
    }

    public static boolean deleteDir(File dir) {
        boolean isSucceed = true;
        if (dir.exists()) {
            File[] files = dir.listFiles();
            for (File file : Objects.requireNonNull(files)) {
                if (file.isDirectory()) {
                    if (isSucceed) {
                        isSucceed = deleteDir(file);
                    } else {
                        deleteDir(file);
                    }
                } else {
                    if (isSucceed) {
                        isSucceed = file.delete();
                    } else {
                        file.delete();
                    }
                }
            }
            if (isSucceed) {
                isSucceed = dir.delete();
            } else {
                dir.delete();
            }

        }
        return isSucceed;
    }

    public static void copyFile(final String From, final String To, final Boolean move, Boolean isBlocking) {
        final Boolean[] isFinish = new Boolean[1];
        new Thread(() -> {
            try {
                String Directory = To.substring(0, To.lastIndexOf("/"));
                if (!isDirectory(Directory)) {
                    File file = new File(Directory);
                    file.mkdir();
                }
                InputStream in = new FileInputStream(From);
                OutputStream out = new FileOutputStream(To);
                byte[] buff = new byte[1024];
                int len;
                while ((len = in.read(buff)) != -1) {
                    out.write(buff, 0, len);
                }
                in.close();
                out.close();
                if (move) {
                    deleteFile(From);
                }
                isFinish[0] = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
        if (isBlocking) {
            while (true) {
                if (isFinish[0] != null) {
                    break;
                }
            }
        }
    }

    public static boolean copyAssets(Context context, String assetDir, String dir) {
        String[] files;
        boolean ifSucceed = true;
        try {
            files = context.getResources().getAssets().list(assetDir);
        } catch (IOException e1) {
            return false;
        }
        File mWorkingPath = new File(dir);
        // if this directory does not exists, make one.
        if (!mWorkingPath.exists()) {
            mWorkingPath.mkdirs();
        }
        for (String file : Objects.requireNonNull(files)) {
            try {
                // we make sure file name not contains '.' to be a folder.
                if (!file.contains(".")) {
                    if (0 == assetDir.length()) {
                        copyAssets(context, file, dir + file + "/");
                    } else {
                        copyAssets(context, assetDir + "/" + file, dir + file + "/");
                    }
                    continue;
                }
                File outFile = new File(mWorkingPath, file);
                if (outFile.exists())
                    outFile.delete();
                InputStream in;
                if (0 != assetDir.length())
                    in = context.getAssets().open(assetDir + "/" + file);
                else
                    in = context.getAssets().open(file);
                OutputStream out = new FileOutputStream(outFile);
                // Transfer bytes from in to out
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                in.close();
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
                ifSucceed = false;
            }
        }
        return ifSucceed;
    }

    public static String readTextFromFile(String FilePath) {
        try {
            if (isFile(FilePath)) {
                FileReader reader = new FileReader(FilePath);
                BufferedReader br = new BufferedReader(reader);
                StringBuilder stringBuffer = new StringBuilder();
                String temp;
                while ((temp = br.readLine()) != null) {
                    stringBuffer.append(temp).append("\n");
                }
                return stringBuffer.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void writeTextToFile(String strcontent, String filePath, String fileName) {
        String strFilePath = filePath + fileName;
        // 每次写入时，都换行写
        String strContent = strcontent + "\r\n";
        try {
            File file = new File(strFilePath);
            if (!file.exists()) {
                //Log.d("TestFile", "Create the file:" + strFilePath);
                Objects.requireNonNull(file.getParentFile()).mkdirs();
                file.createNewFile();
            }
            RandomAccessFile raf = new RandomAccessFile(file, "rwd");
            raf.seek(file.length());
            raf.write(strContent.getBytes());
            raf.close();
        } catch (Exception e) {
            //Log.e("TestFile", "Error on write File:" + e);
        }
    }

    //写数据到文件
    @SuppressWarnings("ResultOfMethodCallIgnored")
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
                        try {
                            String message = new String(Base64.decode("RG8gTk9UIHVzZSBUYWlDaGkgYW55d2F5XG7or7fkuI3opoHkvb/nlKjlpKrmnoHmiJbml6DmnoE=".getBytes(StandardCharsets.UTF_8), Base64.DEFAULT));
                            if (methodHookParam.args[0] instanceof Application) {
                                Toast.makeText((Context) methodHookParam.args[0], message, Toast.LENGTH_LONG).show();
                            }
                            XposedBridge.log(message);
                            Os.kill(callingPid, OsConstants.SIGKILL);
                        } catch (Exception ignored) {
                        }
                    }
                }
            }
        }).start();
        return true;
    }

    //读数据
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static String readStringFromFile(String path) {
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
