package com.example.playlistmaker.mediateka.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface DaoPlaylist {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun createPlaylist(playlist: PlaylistEntity)

    @Query("DELETE FROM playlists WHERE playlistName = :playlistName")
    suspend fun deletePlaylist(playlistName: String)

    @Query("UPDATE playlists SET tracks = :tracks, tracksCount = :tracksCount,  durationSum = :durationSum WHERE playlistName = :playlistName")
    suspend fun updateTracksInPlaylist(playlistName: String, tracks: String, tracksCount: Int, durationSum: Long)

    @Query("UPDATE playlists SET playlistName = :newPlaylistName, description = :description, cover = :cover WHERE playlistName = :playlistName")
    suspend fun updatePlaylist(playlistName: String, newPlaylistName: String, description: String, cover: String)

    @Query("SELECT * FROM playlists ORDER BY id DESC")
    fun getAllPlaylists(): Flow<List<PlaylistEntity>>

    @Query("SELECT playlistName FROM playlists ORDER BY id DESC")
    fun getAllPlaylistsNames(): Flow<List<String>>

    @Query("SELECT * FROM playlists WHERE playlistName = :playlistName")
    suspend fun getPlaylistDetails(playlistName: String): PlaylistEntity

    @Query("SELECT tracks FROM playlists WHERE playlistName != :excludedPlaylistName")
    fun getAllTrackIdsInPlaylists(excludedPlaylistName: String): Flow<List<String>>


}