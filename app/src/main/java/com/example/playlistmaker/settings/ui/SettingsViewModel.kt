package com.example.playlistmaker.settings.ui

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.settings.domain.ThemeInteractor

class SettingsViewModel(private val themeInteractor: ThemeInteractor) : ViewModel() {

    private val _isDarkMode = MutableLiveData<Boolean>()
    val isDarkMode: LiveData<Boolean> get() = _isDarkMode

    private val _intentEvent = MutableLiveData<Event<Intent>>()
    val intentEvent: LiveData<Event<Intent>>
        get() = _intentEvent

    init {
        _isDarkMode.value = themeInteractor.isDarkMode()
    }

    fun switchTheme(enabled: Boolean) {
        themeInteractor.toggleDarkMode(enabled)
        _isDarkMode.value = enabled
        AppCompatDelegate.setDefaultNightMode(
            if (enabled) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        )
    }

    fun shareApp(appLink: String) {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            putExtra(Intent.EXTRA_TEXT, appLink)
            type = "text/plain"
        }
        _intentEvent.value = Event(shareIntent)
    }

    fun contactSupport(email: String, subject: String, text: String) {
        val contactSupportIntent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, text)
        }
        _intentEvent.value = Event(contactSupportIntent)
    }

    fun viewTermsOfUse(termsOfUseLink: String) {
        val termsOfUseIntent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse(termsOfUseLink)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        _intentEvent.value = Event(termsOfUseIntent)
    }

}