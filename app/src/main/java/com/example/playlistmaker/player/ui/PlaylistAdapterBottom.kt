package com.example.playlistmaker.player.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.mediateka.domain.Playlist

class PlaylistAdapterBottom(
    private val playlists: MutableList<Playlist>,
    private val clickListener: AddToPlaylistClickListener
) : RecyclerView.Adapter<PlaylistViewHolderBottom>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistViewHolderBottom {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.linear_playlist, parent, false)
        return PlaylistViewHolderBottom(view)
    }

    override fun getItemCount() = playlists.size

    override fun onBindViewHolder(holder: PlaylistViewHolderBottom, position: Int) {
        val playlist = playlists[position]
        holder.bind(playlist)
        holder.itemView.setOnClickListener {
            clickListener.onAddToPlaylistClick(playlist)
        }
    }

    fun updatePlaylists(newPlaylists: List<Playlist>) {
        playlists.clear()
        playlists.addAll(newPlaylists)
        notifyDataSetChanged()
    }

    fun interface AddToPlaylistClickListener {
        fun onAddToPlaylistClick(playlist: Playlist)
    }
}