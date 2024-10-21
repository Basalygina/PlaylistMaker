package com.example.playlistmaker.mediateka.domain

import android.content.ClipDescription
import com.example.playlistmaker.search.domain.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistInteractor {
    suspend fun createPlaylist(name: String, description: String?, coverImagePath: String?): Boolean
    suspend fun deletePlaylist(playlistName: String)
    suspend fun updatePlaylist(playlist: Playlist, track: Track)
    fun getAllPlaylists(): Flow<List<Playlist>>
    fun getAllPlaylistsNames(): Flow<List<String>>
}