package com.example.playlistmaker.player.ui

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivityPlayerBinding
import com.example.playlistmaker.search.domain.Track
import com.example.playlistmaker.search.domain.Track.Companion.TRACK_DATA
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlayerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPlayerBinding
    private val viewModel: PlayerViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val trackJsonString = intent.getStringExtra(TRACK_DATA) ?: ""
        viewModel.setCurrentTrack(trackJsonString)

        viewModel.trackState.observe(this) { state ->
            when (state) {
                is SelectedTrackState.Loading -> {
                    onLoadingTrackDetails()
                }

                is SelectedTrackState.Content -> {
                    setupTrackDetails(state.trackModel)
                }

                is SelectedTrackState.Error -> {
                    binding.albumCover.setImageResource(R.drawable.error_connection)
                }
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

        binding.buttonFavorite.setOnClickListener {
            viewModel.toggleFavorite()
        }

        binding.playerToolbar.setNavigationOnClickListener {
            onBackPressed()
        }

    }

    private fun onLoadingTrackDetails() {
        binding.buttonPlay.isVisible = false
    }

    private fun preparePlayerUi() {
        binding.playedTime.setText(R.string.timer_start_time)
        binding.buttonPlay.setImageResource(R.drawable.ic_play)
        binding.buttonPlay.isVisible = true
    }

    private fun setupTrackDetails(track: Track) {
        Log.d("PMtest", "..setupTrackDetails")
        binding.trackName.text = track.trackName
        binding.artistName.text = track.artistName
        binding.trackTimeData.text = track.trackTimeString
        binding.collectionData.text = track.collectionName
        binding.yearData.text = track.releaseDate.substring(0, 4)
        binding.genreData.text = track.primaryGenreName
        binding.countryData.text = track.country
        if (track.isFavorite) {
            Log.d("PMtest", "track.isFavorite")
            binding.buttonFavorite.setImageResource(R.drawable.ic_fav_selected)
        }  else {
            Log.d("PMtest", "!!track.isFavorite")
            binding.buttonFavorite.setImageResource(R.drawable.ic_favorite)
        }


        Glide.with(this)
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
        binding.buttonPlay.setImageResource(R.drawable.ic_pause)
    }

    private fun pausePlayer() {
        binding.buttonPlay.setImageResource(R.drawable.ic_play)

    }

    override fun onPause() {
        super.onPause()
        viewModel.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.onCleared()
    }
}