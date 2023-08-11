package com.sk.directudhar.utils

import android.content.Context
import android.content.SharedPreferences

/**
 * Created by User on 14-07-2023.
 */
class SharePrefs(private val ctx: Context) {

    private val sharedPreferences: SharedPreferences

    init {
        sharedPreferences = ctx.getSharedPreferences(PREFERENCE, 0)
    }

    fun putString(key: String?, `val`: String?) {
        sharedPreferences.edit().putString(key, `val`).apply()
    }

    fun getString(key: String?): String? {
        return sharedPreferences.getString(key, "")
    }

    fun putInt(key: String?, `val`: Int?) {
        sharedPreferences.edit().putInt(key, `val`!!).apply()
    }

    fun getInt(key: String?): Int {
        return sharedPreferences.getInt(key, 0)
    }

    fun putBoolean(key: String?, `val`: Boolean?) {
        sharedPreferences.edit().putBoolean(key, `val`!!).apply()
    }

    fun getBoolean(key: String?): Boolean {
        return sharedPreferences.getBoolean(key, false)
    }

    fun getDouble(key: String?): Double {
        return sharedPreferences.getString(key, "0")!!.toDouble()
    }

    fun clear() {
        sharedPreferences.edit().clear().apply()
    }

    companion object {
        var PREFERENCE = "SkDirectSDK"
        var TOKEN = "token"
        var SEQUENCENO = "SequenceNo"
        var LEAD_MASTERID = "LeadMasterId"
        var MOBILE_NUMBER = "MobileNumber"
        private var instance: SharePrefs? = null

        fun getInstance(ctx: Context): SharePrefs? {
            if (instance == null) {
                instance = SharePrefs(ctx)
            }
            return instance
        }

        fun getStringSharedPreferences(context: Context, name: String?): String? {
            val settings = context.getSharedPreferences(PREFERENCE, 0)
            return settings.getString(name, "")
        }

        // for username string preferences
        fun setStringSharedPreference(context: Context, name: String?, value: String?) {
            val settings = context.getSharedPreferences(PREFERENCE, 0)
            val editor = settings.edit()
            editor.putString(name, value)
            editor.apply()
        }
    }
}