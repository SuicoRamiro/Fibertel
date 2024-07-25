package com.example.fibertel.model

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson

class UserManager(context: Context) {
    private val preferences: SharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()

    var currentUser: User?
        get() {
            val userJson = preferences.getString("current_user", null)
            return userJson?.let { gson.fromJson(it, User::class.java) }
        }
        set(user) {
            val editor = preferences.edit()
            if (user != null) {
                val userJson = gson.toJson(user)
                editor.putString("current_user", userJson)
            } else {
                editor.remove("current_user")
            }
            editor.apply()
        }
}
