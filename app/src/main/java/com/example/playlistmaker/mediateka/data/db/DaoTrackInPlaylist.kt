package com.example.playlistmaker.mediateka.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface DaoTrackInPlaylist {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addTrackInDatabase(track: TrackInPlaylistEntity)

    @Query("SELECT * FROM tracks_in_playlist WHERE trackId IN (:trackIds)")
    fun fetchTracksByIds(trackIds: List<Int>): Flow<List<TrackInPlaylistEntity>>

    @Query("DELETE FROM tracks_in_playlist WHERE trackId = :trackId")
    fun removeTrackFromDatabase(trackId: Int)

}