package com.example.playlistmaker.mediateka.domain

import com.example.playlistmaker.search.domain.Track
import kotlinx.coroutines.flow.Flow

interface FavTracksInteractor {
    suspend fun addToFav(track: Track)
    suspend fun removeFromFav(track: Track)
    suspend fun checkIfFav(track: Track): Boolean
    fun getAllFavTracks(): Flow<List<Track>>
    fun encodeTrackDetails(track: Track): String
}