package com.example.playlistmaker.settings.ui


import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Switch

import android.widget.TextView
import android.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import com.example.playlistmaker.R
import com.example.playlistmaker.config.App

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val app = application as App
        val themeSwitcher = findViewById<Switch>(R.id.theme_switcher)
        themeSwitcher.isChecked = app.isDarkMode
        themeSwitcher.setOnCheckedChangeListener { _, checked ->
            app.switchTheme(checked)
        }

        val settingsToolbar = findViewById<Toolbar>(R.id.settings_toolbar)
        settingsToolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        val share = findViewById<TextView>(R.id.share_app)
        share.setOnClickListener {
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.app_link))
            shareIntent.type = "text/plain"
            startActivity(Intent.createChooser(shareIntent, "Поделиться приложением"))
        }

        val contactSupport = findViewById<TextView>(R.id.contact_support)
        contactSupport.setOnClickListener {
            val contactSupportIntent = Intent(Intent.ACTION_SENDTO)
            contactSupportIntent.data = Uri.parse("mailto:")
            contactSupportIntent.putExtra(
                Intent.EXTRA_EMAIL,
                arrayOf(getString(R.string.default_email))
            )
            contactSupportIntent.putExtra(
                Intent.EXTRA_SUBJECT,
                getString(R.string.email_default_subject)
            )
            contactSupportIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.email_default_text))
            startActivity(contactSupportIntent)
        }

        val termsOfUse = findViewById<TextView>(R.id.terms_of_use)
        termsOfUse.setOnClickListener {
            val termsOfUseIntent = Intent(Intent.ACTION_VIEW)
            termsOfUseIntent.data = Uri.parse(getString(R.string.terms_of_use_link))
            termsOfUseIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(termsOfUseIntent)
        }
    }




}