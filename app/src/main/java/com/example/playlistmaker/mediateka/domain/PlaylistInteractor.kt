package com.example.playlistmaker.mediateka.domain

import com.example.playlistmaker.search.domain.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistInteractor {
    suspend fun createPlaylist(name: String, description: String?, coverImagePath: String?): Boolean
    suspend fun deletePlaylist(playlistName: String)
    suspend fun updateTracksInPlaylist(playlistName: String, track: Track, isAdding: Boolean)
    fun getAllPlaylists(): Flow<List<Playlist>>
    fun getAllPlaylistsNames(): Flow<List<String>>
    suspend fun getPlaylistDetails(playlistName: String): Pair<Playlist, List<Track>>
    suspend fun editPlaylist(
        currentPlaylistName: String,
        newPlaylistName: String,
        description: String,
        cover: String?
    )

}