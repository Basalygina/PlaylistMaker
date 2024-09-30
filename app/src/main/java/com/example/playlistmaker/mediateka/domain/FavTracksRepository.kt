package com.example.playlistmaker.mediateka.domain

import com.example.playlistmaker.search.domain.Track
import kotlinx.coroutines.flow.Flow

interface FavTracksRepository {
    suspend fun addToFav(track: Track)
    suspend fun removeFromFav(track: Track)
    fun getAllFavTracks(): Flow<List<Track>>
}