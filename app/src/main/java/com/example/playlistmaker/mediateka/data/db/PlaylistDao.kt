package com.example.playlistmaker.mediateka.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun createPlaylist(playlist: PlaylistEntity)

    @Query("DELETE FROM playlists WHERE playlistName = :playlistName")
    suspend fun deletePlaylist(playlistName: String)

    @Query("UPDATE playlists SET tracks = :tracks, tracksCount = :tracksCount WHERE playlistName = :playlistName")
    suspend fun updatePlaylist(playlistName: String, tracks: String, tracksCount: Int)

    @Query("SELECT * FROM playlists ORDER BY id DESC")
    fun getAllPlaylists(): Flow<List<PlaylistEntity>>

    @Query("SELECT playlistName FROM playlists ORDER BY id DESC")
    fun getAllPlaylistsNames(): Flow<List<String>>

}