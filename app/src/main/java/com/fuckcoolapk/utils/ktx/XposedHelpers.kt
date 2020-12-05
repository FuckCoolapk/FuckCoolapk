package com.fuckcoolapk.utils.ktx

import de.robv.android.xposed.XposedHelpers
import java.util.*

fun Any?.callMethod(methodName: String): Any? = XposedHelpers.callMethod(this, methodName)
fun Any?.callMethod(methodName: String, vararg args: Any?): Any? = XposedHelpers.callMethod(this, methodName, *args)