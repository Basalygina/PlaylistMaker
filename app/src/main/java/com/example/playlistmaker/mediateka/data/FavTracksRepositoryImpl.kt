package com.example.playlistmaker.mediateka.data

import com.example.playlistmaker.mediateka.data.TrackDbMapper.toTrack
import com.example.playlistmaker.mediateka.data.TrackDbMapper.toTrackEntity
import com.example.playlistmaker.mediateka.data.db.FavTracksDatabase
import com.example.playlistmaker.mediateka.domain.FavTracksRepository
import com.example.playlistmaker.search.domain.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FavTracksRepositoryImpl(val favTracksDatabase: FavTracksDatabase): FavTracksRepository {
    override suspend fun addToFav(track: Track) {
        favTracksDatabase.favDao().addToFav(track.toTrackEntity())
    }

    override suspend fun removeFromFav(track: Track) {
        favTracksDatabase.favDao().removeFromFav(track.toTrackEntity())
    }

    override fun getAllFavTracks(): Flow<List<Track>> {
        return favTracksDatabase.favDao().getAllFavTracks()
            .map { trackEntities ->
                trackEntities.map { it.toTrack() }
            }
    }
}