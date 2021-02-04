package com.fuckcoolapk.utils

import android.os.Handler
import android.os.Looper
import android.widget.Toast
import com.fuckcoolapk.TAG
import de.robv.android.xposed.XposedBridge
import android.util.Log as ALog

object LogUtil {

    private val handler by lazy { Handler(Looper.getMainLooper()) }
    private val toast by lazy { Toast.makeText(CoolapkContext.activity, "", Toast.LENGTH_SHORT) }

    fun toast(msg: String, force: Boolean) {
        if (!force && !OwnSP.ownSP.getBoolean("showLogToast", false)) return
        handler.post {
            toast.setText(msg)
            toast.show()
        }
    }

    @JvmStatic
    private fun doLog(f: (String, String) -> Int, obj: Any?, toXposed: Boolean = false) {
        val str = if (obj is Throwable) ALog.getStackTraceString(obj) else obj.toString()
        if (str.length > maxLength) {
            val chunkCount: Int = str.length / maxLength
            for (i in 0..chunkCount) {
                val max: Int = 4000 * (i + 1)
                if (max >= str.length) {
                    doLog(f, str.substring(maxLength * i))
                } else {
                    doLog(f, str.substring(maxLength * i, max))
                }
            }
        } else {
            f(TAG, str)
            toast(str,false)
            if (toXposed)
                XposedBridge.log("$TAG : $str")
        }
    }

    @JvmStatic
    fun d(obj: Any?) {
        doLog(ALog::d, obj)
    }

    @JvmStatic
    fun i(obj: Any?) {
        doLog(ALog::i, obj)
    }

    @JvmStatic
    fun e(obj: Any?) {
        doLog(ALog::e, obj, true)
    }

    @JvmStatic
    fun v(obj: Any?) {
        doLog(ALog::v, obj)
    }

    @JvmStatic
    fun w(obj: Any?) {
        doLog(ALog::w, obj)
    }

    private const val maxLength = 4000
}