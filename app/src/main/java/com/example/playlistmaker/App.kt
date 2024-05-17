package com.example.playlistmaker

import android.app.Application
import android.content.res.Configuration
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM

class App : Application() {
    var darkTheme = false

    override fun onCreate() {
        super.onCreate()
        val sharedPref = getSharedPreferences(PM_PREFERENCES, MODE_PRIVATE)
        if (sharedPref.contains(NIGHT_MODE_KEY)) {
            darkTheme = sharedPref.getBoolean(NIGHT_MODE_KEY, false)
            switchTheme(darkTheme)
        } else {
            val uiModeFlags = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
            AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_FOLLOW_SYSTEM)
            darkTheme = uiModeFlags == Configuration.UI_MODE_NIGHT_YES
        }
    }

    fun switchTheme(darkThemeEnabled: Boolean) {
        val sharedPref = getSharedPreferences(PM_PREFERENCES, MODE_PRIVATE)
        darkTheme = darkThemeEnabled
        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
        sharedPref.edit()
            .putBoolean(NIGHT_MODE_KEY, darkTheme)
            .apply()
    }

    companion object {
        const val PM_PREFERENCES = "PM_preferences"
        const val NIGHT_MODE_KEY = "Night_mode_key"
        const val SEARCH_HISTORY_KEY = "Search_history_key"
    }
}

