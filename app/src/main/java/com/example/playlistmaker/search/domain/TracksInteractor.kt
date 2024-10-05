package com.example.playlistmaker.search.domain

import com.example.playlistmaker.player.domain.SelectedTrackRepository
import kotlinx.coroutines.flow.Flow


interface TracksInteractor {

    fun searchTracks (expression: String): Flow<MutableList<Track>>

    suspend fun getSearchHistory(): MutableList<Track>
    suspend fun clearSearchHistory()
    suspend fun addToSearchHistory(track: Track)
    fun encodeTrackDetails(track: Track): String

}