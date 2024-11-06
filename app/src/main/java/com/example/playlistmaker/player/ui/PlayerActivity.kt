package com.example.playlistmaker.player.ui

import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivityPlayerBinding
import com.example.playlistmaker.mediateka.ui.playlists.CreatePlaylistFragment
import com.example.playlistmaker.search.domain.Track
import com.example.playlistmaker.search.domain.Track.Companion.TRACK_DATA
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlayerActivity : AppCompatActivity() {
    private val viewModel: PlayerViewModel by viewModel()
    private lateinit var binding: ActivityPlayerBinding
    private lateinit var adapter: PlaylistAdapterBottom
    private lateinit var currentTrack: Track

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
                    getPlaylists()
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

        viewModel.playlists.observe(this) { playlists ->
            adapter.updatePlaylists(playlists)
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
        val bottomSheetBehavior = BottomSheetBehavior.from(binding.bottomSheetContainer).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
        }
        binding.buttonAddToPlaylist.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        }

        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        binding.overlay.visibility = View.GONE
                    }

                    else -> {
                        binding.overlay.visibility = View.VISIBLE
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                binding.overlay.alpha = slideOffset
            }
        })

        adapter = PlaylistAdapterBottom(mutableListOf()) { playlist ->
            val isAdded = viewModel.addToPlaylist(currentTrack, playlist)
            showCustomToast(
                getString(
                    when (isAdded) {
                        true -> R.string.added_to_playlist
                        false -> R.string.already_added_to_playlist
                    },
                    playlist.playlistName
                )
            )
            viewModel.getAllPlaylists()
            if (isAdded) {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            }
        }
        binding.playlistRecycler.adapter = adapter



        binding.newPlaylist.setOnClickListener {
            binding.playerFragmentContainer.isVisible = true
            supportFragmentManager.beginTransaction()
                .replace(R.id.player_fragment_container, CreatePlaylistFragment())
                .addToBackStack(null)
                .commit()
        }


    }

    private fun showCustomToast(message: String) {
        val layout = layoutInflater.inflate(R.layout.playlist_toast, null)
        val toastTextView = layout.findViewById<TextView>(R.id.toast_text)
        toastTextView.text = message

        Toast(this)
            .apply {
                duration = Toast.LENGTH_SHORT
                view = layout
                setGravity(Gravity.FILL_HORIZONTAL or Gravity.BOTTOM, 0, 100)
            }
            .show()
    }

    private fun getPlaylists() {
        lifecycleScope.launch {
            viewModel.getAllPlaylists()
        }
    }

    private fun onLoadingTrackDetails() {
        binding.playerElementsGroup.isVisible = false
    }

    private fun preparePlayerUi() {
        binding.playedTime.setText(R.string.timer_start_time)
        binding.buttonPlay.setImageResource(R.drawable.ic_play)
        binding.playerElementsGroup.isVisible = true
    }

    private fun setupTrackDetails(track: Track) {
        currentTrack = track
        with(binding) {
            trackName.text = track.trackName
            artistName.text = track.artistName
            trackTimeData.text = track.trackTimeString
            collectionData.text = track.collectionName
            yearData.text = track.releaseDate.substring(0, 4)
            genreData.text = track.primaryGenreName
            countryData.text = track.country
        }
        if (track.isFavorite) {
            binding.buttonFavorite.setImageResource(R.drawable.ic_fav_selected)
        } else {
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