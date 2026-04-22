package com.leo.zamtrivia.util

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate

object ThemeManager {
    private const val PREFS_NAME = "theme_prefs"
    private const val KEY_IS_DARK_MODE = "is_dark_mode"

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun isDarkMode(context: Context): Boolean {
        return getPrefs(context).getBoolean(KEY_IS_DARK_MODE, false)
    }

    fun setDarkMode(context: Context, isDark: Boolean) {
        getPrefs(context).edit().putBoolean(KEY_IS_DARK_MODE, isDark).apply()
        applyTheme(isDark)
    }

    fun applyTheme(isDark: Boolean) {
        if (isDark) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }

    fun applySavedTheme(context: Context) {
        applyTheme(isDarkMode(context))
    }
}