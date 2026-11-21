package com.example.karflex.util

import android.content.Context

class SessionManager(context: Context) {
    private val prefs = context.getSharedPreferences("karflex_session", Context.MODE_PRIVATE)

    fun saveUser(userId: Int, username: String, phoneNumber: String) {
        prefs.edit()
            .putInt("user_id", userId)
            .putString("username", username)
            .putString("phone", phoneNumber)
            .putBoolean("is_logged_in", true)
            .apply()
    }

    fun isLoggedIn(): Boolean = prefs.getBoolean("is_logged_in", false)
    fun getUserId(): Int = prefs.getInt("user_id", -1)
    fun getUsername(): String = prefs.getString("username", "") ?: ""
    fun getPhone(): String = prefs.getString("phone", "") ?: ""

    fun logout() {
        prefs.edit().clear().apply()
    }
}
