package com.example.playlistmaker.mediateka.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivityMediaBinding
import com.google.android.material.tabs.TabLayoutMediator

class MediaActivity : AppCompatActivity() {

    private var _binding: ActivityMediaBinding? = null
    private val binding get() = _binding!!
    private lateinit var tabMediator: TabLayoutMediator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMediaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.mediaViewPager.adapter = MediaViewPagerAdapter(supportFragmentManager, lifecycle)

        tabMediator = TabLayoutMediator(binding.mediaTabLayout, binding.mediaViewPager) { tab, position ->
            when(position) {
                0 -> tab.text = getString(R.string.tab_favorite)
                1 -> tab.text = getString(R.string.tab_playlists)
            }
        }
        tabMediator.attach()


        binding.mediaToolbar.setNavigationOnClickListener {
            onBackPressed()
        }

    }
}