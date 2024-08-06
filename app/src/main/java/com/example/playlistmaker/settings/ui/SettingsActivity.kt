package com.example.playlistmaker.settings.ui


import android.content.Intent
import androidx.activity.viewModels
import android.net.Uri
import android.os.Bundle
import android.widget.Switch
import androidx.lifecycle.ViewModelProvider
import android.widget.TextView
import android.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import com.example.playlistmaker.R
import com.example.playlistmaker.config.App
import com.example.playlistmaker.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private val viewModel by viewModels<SettingsViewModel> {
        SettingsViewModel.getViewModelFactory(applicationContext)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.isDarkMode.observe(this) { isDarkMode ->
            binding.themeSwitcher.isChecked = isDarkMode
        }

        binding.themeSwitcher.setOnCheckedChangeListener { _, isChecked ->
            viewModel.switchTheme(isChecked)
        }

        binding.settingsToolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        binding.shareApp.setOnClickListener {
            val appLink = getString(R.string.app_link)
            viewModel.shareApp(appLink)
        }

        binding.contactSupport.setOnClickListener {
            val email = getString(R.string.default_email)
            val subject = getString(R.string.email_default_subject)
            val text = getString(R.string.email_default_text)
            viewModel.contactSupport(email, subject, text)
        }

        binding.termsOfUse.setOnClickListener {
            val termsOfUseLink = getString(R.string.terms_of_use_link)
            viewModel.viewTermsOfUse(termsOfUseLink)
        }

        viewModel.intentEvent.observe(this) { event ->
            event.getContentIfNotHandled()?.let { intent ->
                startActivity(intent)
            }
        }
    }
}