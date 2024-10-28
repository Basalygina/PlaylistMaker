package com.example.playlistmaker.mediateka.ui.playlists

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.mediateka.domain.Playlist

class PlaylistAdapter(
    private val playlists: MutableList<Playlist>,
    private val clickListener: PlaylistClickListener
) : RecyclerView.Adapter<PlaylistViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.grid_playlist, parent, false)
        return PlaylistViewHolder(view)
    }

    override fun getItemCount() = playlists.size

    override fun onBindViewHolder(holder: PlaylistViewHolder, position: Int) {
        holder.bind(playlists[position])
        holder.itemView.setOnClickListener { clickListener.onPlaylistClick(playlists.get(position)) }
    }

    fun updatePlaylists(newPlaylists: List<Playlist>) {
        playlists.clear()
        playlists.addAll(newPlaylists)
        notifyDataSetChanged()
    }
}

fun interface PlaylistClickListener {
    fun onPlaylistClick(playlist: Playlist)
}
