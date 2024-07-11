package com.example.playlistmaker.ui.search

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.models.Track


class TrackViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
    private val trackNameView: TextView
    private val artistNameView: TextView
    private val trackTimeView: TextView
    private val artworkUrl100View: ImageView

    init {
        trackNameView = itemView.findViewById(R.id.trackTitle)
        artistNameView = itemView.findViewById(R.id.artist)
        trackTimeView = itemView.findViewById(R.id.trackTime)
        artworkUrl100View = itemView.findViewById(R.id.albumCover)
    }

    fun bind(model: Track) {
        trackNameView.text = model.trackName
        artistNameView.text = model.artistName
        trackTimeView.text = model.trackTimeString
        Glide.with(itemView)
            .load(model.artworkUrl100)
            .placeholder(R.drawable.placeholder)
            .transform(RoundedCorners(4))
            .into(artworkUrl100View)
    }
}