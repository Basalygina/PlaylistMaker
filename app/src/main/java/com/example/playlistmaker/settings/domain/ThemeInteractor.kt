package com.example.playlistmaker.settings.domain

interface ThemeInteractor {
    fun isDarkMode(): Boolean
    fun toggleDarkMode(enabled: Boolean)
    fun initializeThemeFromPreferences(): Boolean
}