package com.example.playlistmaker.search.domain

import com.example.playlistmaker.player.domain.SelectedTrackRepository


interface TracksInteractor {

    fun searchTracks (expression: String, consumer: TracksConsumer)

    interface TracksConsumer{
        fun consume(foundTracks: List<Track>)
        fun onError(t: Throwable)
    }

    fun getSearchHistory(): MutableList<Track>
    fun clearSearchHistory()
    fun addToSearchHistory(track: Track)
    fun encodeTrackDetails(track: Track): String

}