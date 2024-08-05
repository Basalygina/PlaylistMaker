package com.example.playlistmaker.settings.domain

class ThemeInteractorImpl(
    private val themeRepository: ThemeRepository
) : ThemeInteractor {

    override fun isDarkMode(): Boolean {
        return themeRepository.isDarkMode
    }

    override fun toggleDarkMode(enabled: Boolean) {
        themeRepository.isDarkMode = enabled
    }

    override fun isThemePreferencesExists() =
        themeRepository.isThemePreferencesExists

}