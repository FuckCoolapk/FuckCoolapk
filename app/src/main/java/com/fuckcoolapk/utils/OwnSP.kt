package com.fuckcoolapk.utils

import android.content.Context
import android.content.SharedPreferences

object OwnSP {
    val ownSP: SharedPreferences by lazy { CoolapkContext.context.getSharedPreferences("fuckcoolapk", Context.MODE_PRIVATE) }
    private val ownEditor: SharedPreferences.Editor = ownSP.edit()
    fun set(string: String, any: Any) {
        when (any) {
            is Int -> ownEditor.putInt(string, any)
            is Float -> ownEditor.putFloat(string, any)
            is String -> ownEditor.putString(string, any)
            is Boolean -> ownEditor.putBoolean(string, any)
            is Long -> ownEditor.putLong(string, any)
        }
        ownEditor.apply()
    }
}