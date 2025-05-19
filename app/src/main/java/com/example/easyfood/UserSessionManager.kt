package com.example.easyfood



import android.content.Context
import android.content.SharedPreferences

object UserSessionManager {
    private const val PREF_NAME = "user_session"
    private const val KEY_USER_ID = "user_id"

    fun saveLoggedInUserId(context: Context, userId: Int) {
        val sharedPref: SharedPreferences =
            context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        sharedPref.edit().putInt(KEY_USER_ID, userId).apply()
    }

    fun getLoggedInUserId(context: Context): Int {
        val sharedPref: SharedPreferences =
            context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return sharedPref.getInt(KEY_USER_ID, -1) // -1 if not found
    }

    fun clearSession(context: Context) {
        val sharedPref: SharedPreferences =
            context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        sharedPref.edit().clear().apply()
    }
}
