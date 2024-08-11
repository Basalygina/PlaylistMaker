package com.example.playlistmaker.config

import android.app.Application
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.di.dataModule
import com.example.playlistmaker.di.interactorModule
import com.example.playlistmaker.di.repositoryModule
import com.example.playlistmaker.di.viewModelModule
import com.example.playlistmaker.settings.domain.ThemeInteractor
import org.koin.android.ext.android.getKoin
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@App)
            modules(dataModule, interactorModule, repositoryModule, viewModelModule)
        }

        val themeInteractor: ThemeInteractor = getKoin().get()
        if (themeInteractor.isThemePreferencesExists()) {
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







