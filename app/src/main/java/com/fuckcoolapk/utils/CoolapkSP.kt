package com.fuckcoolapk.utils

import android.content.Context
import android.content.SharedPreferences

object CoolapkSP {
    val coolapkSP: SharedPreferences by lazy { CoolapkContext.context.getSharedPreferences("coolapk_preferences_v7", Context.MODE_PRIVATE) }
    private val coolapkEditor: SharedPreferences.Editor = coolapkSP.edit()
    fun set(string: String, any: Any) {
        when (any) {
            is Int -> coolapkEditor.putInt(string, any)
            is Float -> coolapkEditor.putFloat(string, any)
            is String -> coolapkEditor.putString(string, any)
            is Boolean -> coolapkEditor.putBoolean(string, any)
            is Long -> coolapkEditor.putLong(string, any)
        }
        coolapkEditor.apply()
    }
}