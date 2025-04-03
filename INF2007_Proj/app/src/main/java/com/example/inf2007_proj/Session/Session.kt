package com.example.inf2007_proj.Session

import android.content.Context
import android.content.SharedPreferences

class Session(context: Context) {
    // Initialize variables
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("AppKey", Context.MODE_PRIVATE)
    private val editor: SharedPreferences.Editor = sharedPreferences.edit()

    // Create set login method
    fun setLogin(login: Boolean) {
        editor.putBoolean("KEY_LOGIN", login)
        editor.apply()
    }

    // Create get login method
    fun getLogin(): Boolean {
        return sharedPreferences.getBoolean("KEY_LOGIN", false)
    }

    // Create set username method
    fun setUsername(username: String) {
        editor.putString("KEY_USERNAME", username)
        editor.apply()
    }

    // Create get username method
    fun getUsername(): String {
        return sharedPreferences.getString("KEY_USERNAME", "") ?: ""
    }
}