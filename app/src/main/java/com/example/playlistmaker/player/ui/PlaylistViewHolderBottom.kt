package com.example.playlistmaker.player.ui

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.LinearPlaylistBinding
import com.example.playlistmaker.mediateka.domain.Playlist
import com.example.playlistmaker.mediateka.ui.MediaTextFormatter

class PlaylistViewHolderBottom(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val binding = LinearPlaylistBinding.bind(itemView)

    fun bind(playlist: Playlist) {
        binding.playlistName.text = playlist.playlistName
        binding.tracksCount.text =
            playlist.tracksCount.toString() + " " + MediaTextFormatter.getTrackDeclension(playlist.tracksCount)

        Glide.with(itemView)
            .load(playlist.cover)
            .placeholder(R.drawable.placeholder)
            .transform(RoundedCorners(4))
            .into(binding.coverImage)

    }


}