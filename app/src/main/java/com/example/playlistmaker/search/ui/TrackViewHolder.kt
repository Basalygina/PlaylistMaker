package com.example.playlistmaker.search.ui

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.LinearTrackBinding
import com.example.playlistmaker.search.domain.Track


class TrackViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val binding = LinearTrackBinding.bind(itemView)

    fun bind(model: Track) {
        binding.trackTitle.text = model.trackName
        binding.artist.text = model.artistName
        binding.trackTime.text = model.trackTimeString
        Glide.with(itemView)
            .load(model.artworkUrl100)
            .placeholder(R.drawable.placeholder)
            .transform(RoundedCorners(4))
            .into(binding.albumCover)
    }
}