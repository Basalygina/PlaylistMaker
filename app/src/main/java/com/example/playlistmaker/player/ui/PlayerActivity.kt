package com.example.playlistmaker.player.ui

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivityPlayerBinding
import com.example.playlistmaker.player.domain.PlayerInteractor
import com.example.playlistmaker.search.domain.Track
import com.example.playlistmaker.search.domain.Track.Companion.TRACK_DATA

class PlayerActivity : AppCompatActivity() {
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var binding: ActivityPlayerBinding
    private lateinit var viewModel: PlayerViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val trackJsonString = intent.getStringExtra(TRACK_DATA) ?: ""
        viewModel = ViewModelProvider(
            this,
            PlayerViewModel.getViewModelFactory(trackJsonString)
        )[PlayerViewModel::class.java]

        viewModel.getTrackDetails()

        viewModel.trackState.observe(this) { state ->
            when (state) {
                is SelectedTrackState.Loading -> Unit
                is SelectedTrackState.Content -> {
                    setupTrackDetails(state.trackModel)}
            }
        }

        viewModel.playerState.observe(this) { state ->
            when (state) {
                PlayerScreenState.Prepared -> preparePlayerUi()
                PlayerScreenState.Playing -> startPlayer()
                PlayerScreenState.Paused -> pausePlayer()
            }
        }

        viewModel.currentTime.observe(this) { time ->
            binding.playedTime.text = time
        }

        binding.buttonPlay.setOnClickListener {
            viewModel.togglePlayPause()
        }

        binding.playerToolbar.setNavigationOnClickListener {
            onBackPressed()
        }

    }

    private fun preparePlayerUi() {
        binding.playedTime.setText(R.string.timer_start_time)
        binding.buttonPlay.setImageResource(R.drawable.ic_play)
    }

    private fun setupTrackDetails(track: Track) {
        binding.trackName.text = track.trackName
        binding.artistName.text = track.artistName
        binding.trackTimeData.text = track.trackTimeString
        binding.collectionData.text = track.collectionName
        binding.yearData.text = track.releaseDate.substring(0, 4)
        binding.genreData.text = track.primaryGenreName
        binding.countryData.text = track.country

        Glide.with(applicationContext)
            .load(track.artworkUrl512)
            .placeholder(R.drawable.placeholder_large)
            .centerCrop()
            .transform(RoundedCorners(8))
            .into(binding.albumCover)

        if (track.collectionName.isEmpty()) {
            val constraintSet = ConstraintSet()
            constraintSet.clone(binding.playerLayout)
            binding.collectionLabel.text = ""
            constraintSet.connect(
                R.id.year_label,
                ConstraintSet.TOP,
                R.id.h_guideLine_8,
                ConstraintSet.BOTTOM
            )
            constraintSet.connect(
                R.id.genre_label,
                ConstraintSet.TOP,
                R.id.h_guideLine_9,
                ConstraintSet.BOTTOM
            )
            constraintSet.connect(
                R.id.country_label,
                ConstraintSet.TOP,
                R.id.h_guideLine_10,
                ConstraintSet.BOTTOM
            )
            constraintSet.applyTo(binding.playerLayout)
        }
    }

    private fun startPlayer() {
        viewModel.play()
        binding.buttonPlay.setImageResource(R.drawable.ic_pause)
    }

    private fun pausePlayer() {
        viewModel.pause()
        binding.buttonPlay.setImageResource(R.drawable.ic_play)

    }

    override fun onPause() {
        super.onPause()
        pausePlayer()
    }
}