package com.fuckcoolapk.utils

import android.content.Context
import android.content.SharedPreferences

object CoolapkSP {
    val coolapkSP: SharedPreferences by lazy { CoolapkContext.context.getSharedPreferences("coolapk_preferences_v7", Context.MODE_PRIVATE) }
    private val coolapkEditor: SharedPreferences.Editor = coolapkSP.edit()
    fun set(key: String, any: Any) {
        when (any) {
            is Int -> coolapkEditor.putInt(key, any)
            is Float -> coolapkEditor.putFloat(key, any)
            is String -> coolapkEditor.putString(key, any)
            is Boolean -> coolapkEditor.putBoolean(key, any)
            is Long -> coolapkEditor.putLong(key, any)
        }
        coolapkEditor.apply()
    }

    fun remove(key: String) {
        coolapkEditor.remove(key)
        coolapkEditor.apply()
    }
}