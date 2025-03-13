package com.geby.listifyapplication.user

import android.content.Context

internal class UserPreference(context: Context) {

    companion object {
        private const val PREFS_NAME = "user_pref"
        private const val NAME = "name"
    }

    private val preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun setUser(value: UserModel) {
        preferences.edit().putString(NAME, value.name).apply()
    }

    fun getUser(): UserModel {
        return UserModel(name = preferences.getString(NAME, "Guest") ?: "Guest")
    }
}