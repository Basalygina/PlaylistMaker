package com.example.playlistmaker.mediateka.domain

import com.example.playlistmaker.search.domain.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistRepository {
    suspend fun createPlaylist(playlist: Playlist)
    suspend fun deletePlaylist(playlistName: String)
    suspend fun updatePlaylist(playlist: Playlist, track: Track)
    fun getAllPlaylists(): Flow<List<Playlist>>
    fun getAllPlaylistsNames(): Flow<List<String>>
}