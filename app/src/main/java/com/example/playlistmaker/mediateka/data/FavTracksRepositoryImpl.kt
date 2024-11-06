package com.example.playlistmaker.mediateka.data

import com.example.playlistmaker.mediateka.data.TrackDbMapper.toTrack
import com.example.playlistmaker.mediateka.data.TrackDbMapper.toTrackEntity

import com.example.playlistmaker.mediateka.data.db.PlaylistDatabase
import com.example.playlistmaker.mediateka.domain.FavTracksRepository
import com.example.playlistmaker.search.domain.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class FavTracksRepositoryImpl(val playlistDatabase: PlaylistDatabase) : FavTracksRepository {
    override suspend fun addToFav(track: Track) {
        playlistDatabase.daoFav().addToFav(track.toTrackEntity())
    }

    override suspend fun removeFromFav(track: Track) {
        playlistDatabase.daoFav().removeFromFav(track.trackId)
    }

    override suspend fun checkIfFav(track: Track): Boolean {
        return getAllFavTracks()
            .map { tracks -> tracks.any { it.trackId == track.trackId } }
            .first()
    }


    override fun getAllFavTracks(): Flow<List<Track>> {
        return playlistDatabase.daoFav().getAllFavTracks()
            .map { trackEntities ->
                trackEntities.map { it.toTrack() }
            }
    }
}