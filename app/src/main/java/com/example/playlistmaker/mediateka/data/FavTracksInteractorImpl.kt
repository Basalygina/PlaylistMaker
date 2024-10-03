package com.example.playlistmaker.mediateka.data

import com.example.playlistmaker.mediateka.domain.FavTracksInteractor
import com.example.playlistmaker.mediateka.domain.FavTracksRepository
import com.example.playlistmaker.player.domain.SelectedTrackRepository
import com.example.playlistmaker.search.domain.Track
import kotlinx.coroutines.flow.Flow

class FavTracksInteractorImpl(
    val favTracksRepository: FavTracksRepository,
    val selectedTrackRepository: SelectedTrackRepository
) : FavTracksInteractor {
    override suspend fun addToFav(track: Track) {
        favTracksRepository.addToFav(track)
    }

    override suspend fun removeFromFav(track: Track) {
        favTracksRepository.removeFromFav(track)
    }

    override suspend fun checkIfFav(track: Track): Boolean {
        return favTracksRepository.checkIfFav(track)
    }

    override fun getAllFavTracks(): Flow<List<Track>> {
        return favTracksRepository.getAllFavTracks()
    }

    override fun encodeTrackDetails(track: Track): String {
        return selectedTrackRepository.encodeTrackDetails(track)
    }
}