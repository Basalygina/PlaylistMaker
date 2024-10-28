package com.example.playlistmaker.search.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.search.domain.Track

class TrackAdapter(
    private val tracks: MutableList<Track>,
    private val clickListener: TrackClickListener,
    private val longClickListener: TrackLongClickListener? = null
) : RecyclerView.Adapter<TrackViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.linear_track, parent, false)
        return TrackViewHolder(view)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(tracks[position])

        holder.itemView.setOnClickListener { clickListener.onTrackClick(tracks.get(position)) }
        holder.itemView.setOnLongClickListener {
            longClickListener?.onLongTrackClick(tracks[position]) != null
        }
    }

    override fun getItemCount() = tracks.size

    fun updateTracks(newTracks: List<Track>) {
        tracks.clear()
        tracks.addAll(newTracks)
        notifyDataSetChanged()
    }

    fun clearTracks() {
        tracks.clear()
        notifyDataSetChanged()
    }

    fun interface TrackClickListener {
        fun onTrackClick(track: Track)
    }

    fun interface TrackLongClickListener {
        fun onLongTrackClick(track: Track)
    }

}