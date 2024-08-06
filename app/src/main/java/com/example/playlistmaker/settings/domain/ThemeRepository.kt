package com.example.playlistmaker.settings.domain

interface ThemeRepository {
    var isDarkMode: Boolean
    val isThemePreferencesExists: Boolean
}