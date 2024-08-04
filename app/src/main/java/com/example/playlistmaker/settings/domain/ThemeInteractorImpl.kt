package com.example.playlistmaker.settings.domain

import android.util.Log

class ThemeInteractorImpl(
    private val themeRepository: ThemeRepository
) : ThemeInteractor {

    override fun isDarkMode(): Boolean {
        return themeRepository.isDarkMode
    }

    override fun toggleDarkMode(enabled: Boolean) {
        themeRepository.isDarkMode = enabled
    }

    override fun initializeThemeFromPreferences(): Boolean {
        return if (themeRepository.isThemePreferencesExists) {
            Log.d("PMtest", "initializeThemeFromPreferences, ${themeRepository.isThemePreferencesExists}")
            toggleDarkMode(themeRepository.isDarkMode)
            true
        } else {
            false
        }
    }

}