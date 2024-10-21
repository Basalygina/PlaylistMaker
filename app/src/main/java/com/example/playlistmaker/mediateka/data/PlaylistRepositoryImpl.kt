package com.example.playlistmaker.mediateka.data

import android.util.Log
import com.example.playlistmaker.mediateka.data.PlaylistDbMapper.toPlaylist
import com.example.playlistmaker.mediateka.data.PlaylistDbMapper.toPlaylistEntity
import com.example.playlistmaker.mediateka.data.db.PlaylistDatabase
import com.example.playlistmaker.mediateka.domain.Playlist
import com.example.playlistmaker.mediateka.domain.PlaylistRepository
import com.example.playlistmaker.search.domain.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PlaylistRepositoryImpl(val playlistDatabase: PlaylistDatabase) : PlaylistRepository {
    override suspend fun createPlaylist(playlist: Playlist) {
        playlistDatabase.playlistDao().createPlaylist(playlist.toPlaylistEntity())
    }

    override suspend fun deletePlaylist(playlistName: String) {
        playlistDatabase.playlistDao().deletePlaylist(playlistName)
    }

    override suspend fun updatePlaylist(playlist: Playlist, track: Track) {
        val tracksList = PlaylistDbMapper.toTracksList(playlist.tracks).toMutableList()
        tracksList.add(track.trackId)
        val updatedTracks = PlaylistDbMapper.fromTracksList(tracksList.toList())
        playlistDatabase.playlistDao()
            .updatePlaylist(playlist.playlistName, updatedTracks, tracksList.size)
    }

    override fun getAllPlaylists(): Flow<List<Playlist>> {
        return playlistDatabase.playlistDao().getAllPlaylists()
            .map { playlistEntities ->
                playlistEntities.map { it.toPlaylist() }
            }
    }

    override fun getAllPlaylistsNames(): Flow<List<String>> {
        return playlistDatabase.playlistDao().getAllPlaylistsNames()
    }

}