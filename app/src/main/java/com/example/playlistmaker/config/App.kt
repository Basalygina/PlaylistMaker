package com.example.playlistmaker.config

import android.app.Application

import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
import com.example.playlistmaker.config.domain.ThemeRepository
import com.example.playlistmaker.creator.Creator

class App : Application() {
    var isDarkMode = false
    lateinit var themeRepo: ThemeRepository

    override fun onCreate() {
        super.onCreate()
        themeRepo = Creator.getThemeRepository(this)
        if (themeRepo.isThemePreferencesExists) {
                isDarkMode = themeRepo.isDarkMode
                switchTheme(isDarkMode)
            } else {
                val uiModeFlags = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
                AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_FOLLOW_SYSTEM)
                isDarkMode = uiModeFlags == Configuration.UI_MODE_NIGHT_YES
            }
        }

    fun switchTheme(darkThemeEnabled: Boolean) {
        isDarkMode = darkThemeEnabled
        themeRepo.isDarkMode = isDarkMode
        AppCompatDelegate.setDefaultNightMode(
            if (isDarkMode) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )

    }

    companion object {
        const val PM_PREFERENCES = "PM_preferences"
        const val NIGHT_MODE_KEY = "Key_night_mode"
        const val TAG = "PMtest"
    }
}

