package com.example.playlistmaker.mediateka.data

import android.util.Log
import com.example.playlistmaker.mediateka.data.PlaylistDbMapper.toTracksList
import com.example.playlistmaker.mediateka.data.TrackDbMapper.toTrack
import com.example.playlistmaker.mediateka.data.TrackDbMapper.toTrackInPlaylistEntity
import com.example.playlistmaker.mediateka.data.db.PlaylistDatabase
import com.example.playlistmaker.mediateka.domain.TrackInPlaylistRepository
import com.example.playlistmaker.search.domain.Track
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class TrackInPlaylistRepositoryImpl(val playlistDatabase: PlaylistDatabase) :
    TrackInPlaylistRepository {
    override fun fetchTracksByIds(trackIds: List<Int>): Flow<List<Track>> {
        return playlistDatabase.daoTrackInPlaylist().fetchTracksByIds(trackIds)
            .map { tracksInPlaylists ->
                tracksInPlaylists.map { it.toTrack() }
            }
    }

    override suspend fun addTrackInDatabase(track: Track) = withContext(Dispatchers.IO)  {
        playlistDatabase.daoTrackInPlaylist().addTrackInDatabase(track.toTrackInPlaylistEntity())
    }

    override suspend fun removeTrackFromDatabase(trackId: Int, playlistName: String) = withContext(Dispatchers.IO) {
        val allTrackIds = playlistDatabase.daoPlaylist()
            .getAllTrackIdsInPlaylists(playlistName)
            .first()
            .flatMap { toTracksList(it) }
        if (trackId !in allTrackIds) {
            playlistDatabase.daoTrackInPlaylist().removeTrackFromDatabase(trackId)
        }
    }

}