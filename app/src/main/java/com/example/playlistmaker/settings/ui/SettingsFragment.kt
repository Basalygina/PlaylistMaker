package com.example.playlistmaker.settings.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentSettingsBinding
import org.koin.androidx.viewmodel.ext.android.viewModel


class SettingsFragment : Fragment() {

    private lateinit var binding: FragmentSettingsBinding
    private val viewModel: SettingsViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSettingsBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.isDarkMode.observe(this) { isDarkMode ->
            binding.themeSwitcher.isChecked = isDarkMode
        }

        binding.themeSwitcher.setOnCheckedChangeListener { _, isChecked ->
            viewModel.switchTheme(isChecked)
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