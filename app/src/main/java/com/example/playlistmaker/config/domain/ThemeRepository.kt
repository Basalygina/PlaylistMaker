package com.example.playlistmaker.config.domain

interface ThemeRepository {
    var isDarkMode: Boolean
    val isThemePreferencesExists: Boolean
}