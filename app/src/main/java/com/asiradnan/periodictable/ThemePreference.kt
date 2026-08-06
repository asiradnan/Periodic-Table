package com.asiradnan.periodictable

import android.content.Context
import androidx.core.content.edit

object ThemePreference {
    private const val PREF_NAME = "AppPreferences"
    private const val KEY_DARK_MODE = "dark_mode"
    private const val KEY_LANGUAGE = "language"

    fun saveDarkMode(context: Context, isDarkMode: Boolean) {
        val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        sharedPreferences.edit { putBoolean(KEY_DARK_MODE, isDarkMode) }
    }

    fun getDarkMode(context: Context): Boolean {
        val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getBoolean(KEY_DARK_MODE, false)
    }

    fun saveLanguage(context: Context, isEnglish: Boolean) {
        val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        sharedPreferences.edit { putBoolean(KEY_LANGUAGE, isEnglish) }
    }

    fun getLanguage(context: Context): Boolean {
        val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getBoolean(KEY_LANGUAGE, true)
    }
}
