package com.example.playlistmaker.mediateka.data

import com.example.playlistmaker.mediateka.domain.FavTracksInteractor
import com.example.playlistmaker.mediateka.domain.FavTracksRepository
import com.example.playlistmaker.search.domain.Track
import kotlinx.coroutines.flow.Flow

class FavTracksInteractorImpl(val favTracksRepository: FavTracksRepository) : FavTracksInteractor {
    override suspend fun addToFav(track: Track) {
        favTracksRepository.addToFav(track)
    }

    override suspend fun removeFromFav(track: Track) {
        favTracksRepository.removeFromFav(track)
    }

    override fun getAllFavTracks(): Flow<List<Track>> {
        return favTracksRepository.getAllFavTracks()
    }
}