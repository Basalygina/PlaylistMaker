package com.example.playlistmaker.mediateka.domain

import com.example.playlistmaker.search.domain.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistRepository {
    suspend fun createPlaylist(playlist: Playlist)
    suspend fun deletePlaylist(playlistName: String)
    suspend fun updateTracksInPlaylist(playlistName: String, track: Track, isAdding: Boolean)
    fun getAllPlaylists(): Flow<List<Playlist>>
    fun getAllPlaylistsNames(): Flow<List<String>>
    suspend fun getPlaylistDetails(playlistName: String): Playlist
    suspend fun updatePlaylist(playlistName: String, newPlaylistName: String, description: String, cover: String?)
}