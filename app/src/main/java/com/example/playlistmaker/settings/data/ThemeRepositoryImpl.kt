package com.example.playlistmaker.settings.data

import android.content.Context
import com.example.playlistmaker.config.App
import com.example.playlistmaker.settings.domain.ThemeRepository

class ThemeRepositoryImpl(context: Context) : ThemeRepository {
    private val sharedPref = context.getSharedPreferences(App.PM_PREFERENCES, Context.MODE_PRIVATE)

    override var isDarkMode: Boolean
        get() = sharedPref.getBoolean(App.NIGHT_MODE_KEY, false)
        set(value) {
            sharedPref.edit()
                .putBoolean(App.NIGHT_MODE_KEY, value)
                .apply()
        }

    override var isThemePreferencesExists =
        sharedPref.contains(App.NIGHT_MODE_KEY)

}


