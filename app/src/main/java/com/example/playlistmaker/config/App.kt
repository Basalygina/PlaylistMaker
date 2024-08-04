package com.example.playlistmaker.config

import android.app.Application
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.creator.Creator

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        val themeInteractor = Creator.provideThemeInteractor(this)
        if (themeInteractor.initializeThemeFromPreferences()) {
            AppCompatDelegate.setDefaultNightMode(
                if (themeInteractor.isDarkMode()) {
                    AppCompatDelegate.MODE_NIGHT_YES
                } else {
                    AppCompatDelegate.MODE_NIGHT_NO
                }
            )
        } else {
            val uiModeFlags = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
            AppCompatDelegate.setDefaultNightMode(
                if (uiModeFlags == Configuration.UI_MODE_NIGHT_YES) {
                    AppCompatDelegate.MODE_NIGHT_YES
                } else {
                    AppCompatDelegate.MODE_NIGHT_NO
                }
            )
        }
    }

    companion object {
        const val PM_PREFERENCES = "PM_preferences"
        const val NIGHT_MODE_KEY = "Key_night_mode"
        const val TAG = "PMtest"
    }
}







