package com.example.playlistmaker.mediateka.ui.playlists

import android.view.View
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.GridPlaylistBinding
import com.example.playlistmaker.mediateka.domain.Playlist
import com.example.playlistmaker.mediateka.ui.MediaTextFormatter

class PlaylistViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val binding = GridPlaylistBinding.bind(itemView)

    fun bind(playlist: Playlist) {
        binding.playlistName.text = playlist.playlistName
        binding.tracksCount.text =
            playlist.tracksCount.toString() + " " + MediaTextFormatter.getTrackDeclension(playlist.tracksCount)
        if (playlist.cover != null) {
            binding.coverImage.isVisible = true
            binding.coverPlaceholder.isVisible = false
            Glide.with(itemView)
                .load(playlist.cover)
                .placeholder(R.drawable.placeholder_medium)
                .into(binding.coverImage)
        } else {
            binding.coverImage.isVisible = false
            binding.coverPlaceholder.isVisible = true
        }
    }


}