package com.example.playlistmaker.settings.data

import android.content.SharedPreferences
import com.example.playlistmaker.config.App
import com.example.playlistmaker.settings.domain.ThemeRepository

class ThemeRepositoryImpl(
    private val sharedPref: SharedPreferences
) : ThemeRepository {

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


