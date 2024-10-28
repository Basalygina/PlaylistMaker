package com.example.playlistmaker.mediateka.domain

import com.example.playlistmaker.search.domain.Track
import kotlinx.coroutines.flow.Flow

interface TrackInPlaylistRepository {
    fun fetchTracksByIds(trackIds: List<Int>): Flow<List<Track>>
    suspend fun addTrackInDatabase(track: Track)
    suspend fun removeTrackFromDatabase(trackId: Int)
}