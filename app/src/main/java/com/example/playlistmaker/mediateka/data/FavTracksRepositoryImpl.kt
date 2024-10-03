package com.example.playlistmaker.mediateka.data

import android.util.Log
import com.example.playlistmaker.mediateka.data.TrackDbMapper.toTrack
import com.example.playlistmaker.mediateka.data.TrackDbMapper.toTrackEntity
import com.example.playlistmaker.mediateka.data.db.FavTracksDatabase
import com.example.playlistmaker.mediateka.domain.FavTracksRepository
import com.example.playlistmaker.search.domain.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class FavTracksRepositoryImpl(val favTracksDatabase: FavTracksDatabase): FavTracksRepository {
    override suspend fun addToFav(track: Track) {
        Log.d("PMtest", "...addToFav")
        favTracksDatabase.favDao().addToFav(track.toTrackEntity())
    }

    override suspend fun removeFromFav(track: Track) {
        Log.d("PMtest", "...removeFromFav")
        favTracksDatabase.favDao().removeFromFav(track.trackId)
    }

    override suspend fun checkIfFav(track: Track): Boolean {
        Log.d("PMtest", "...checkIfFav")
        return getAllFavTracks()
            .map { tracks -> tracks.any { it.trackId == track.trackId } }
            .first()
        }


    override fun getAllFavTracks(): Flow<List<Track>> {
        Log.d("PMtest", "...getAllFavTracks")
        return favTracksDatabase.favDao().getAllFavTracks()
            .map { trackEntities ->
                trackEntities.map { it.toTrack() }
            }
    }
}