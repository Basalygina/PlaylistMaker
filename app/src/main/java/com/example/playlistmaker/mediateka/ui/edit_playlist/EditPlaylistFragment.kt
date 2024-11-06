package com.example.playlistmaker.mediateka.ui.edit_playlist

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.config.App.Companion.TAG
import com.example.playlistmaker.main.ui.RootActivity
import com.example.playlistmaker.mediateka.domain.Playlist
import com.example.playlistmaker.mediateka.domain.Playlist.Companion.ARG_PLAYLIST_NAME
import com.example.playlistmaker.mediateka.ui.playlist_details.SelectedPlaylistState
import com.example.playlistmaker.mediateka.ui.playlists.CreatePlaylistFragment
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class EditPlaylistFragment : CreatePlaylistFragment() {
    private val viewModel: EditPlaylistViewModel by viewModel()
    private var currentPlaylistName: String? = null
    private var currentPlaylistCover: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        arguments?.let {
            currentPlaylistName = it.getString(ARG_PLAYLIST_NAME)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.buttonCreate.isEnabled = false
        viewModel.getPlaylistDetails(currentPlaylistName)
        viewModel.playlistState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is SelectedPlaylistState.Content -> setupPlaylistDetails(state.playlistModel)
                SelectedPlaylistState.Error -> showCustomToast(getString(R.string.error_loading_playlist))
                SelectedPlaylistState.Loading -> Unit
                else -> showCustomToast(getString(R.string.something_went_wrong))
            }
        }

        binding.buttonCreate.setOnClickListener {
            editPlaylist()
        }
    }

    private fun setupPlaylistDetails(playlist: Playlist) {
        val currentActivity = activity
        if (currentActivity is RootActivity) {
            currentActivity.binding.bottomElementsGroup.isVisible = false
        }
        with(binding) {
            createToolbar.title = getString(R.string.edit)
            buttonCreate.text = getString(R.string.save)
            playlistName.setText(playlist.playlistName)
            playlistDescription.setText(playlist.description)
        }
        currentPlaylistCover = playlist.cover

        if (playlist.cover != null) {
            Glide.with(this)
                .load(playlist.cover)
                .placeholder(R.drawable.placeholder_large)
                .centerCrop()
                .into(binding.playlistCover)
        } else {
            binding.playlistCover.setImageResource(R.drawable.placeholder_large)
        }
    }


    fun editPlaylist() {
        val newPlaylistName = binding.playlistName.text.toString()
        val description = binding.playlistDescription.text.toString()
        if (selectedImageUri != null) {
            coverImagePath = viewModel.saveCover(selectedImageUri!!)
        } else {
            coverImagePath = currentPlaylistCover
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.editPlaylist(
                currentPlaylistName!!,
                newPlaylistName,
                description,
                coverImagePath
            )

            val bundle = Bundle().apply {
                putString(ARG_PLAYLIST_NAME, newPlaylistName)
            }
            val navOptions = NavOptions.Builder()
                .setPopUpTo(R.id.playlistDetailsFragment, inclusive = true)
                .build()

            findNavController().navigate(R.id.playlistDetailsFragment, bundle, navOptions)


        }
    }

}