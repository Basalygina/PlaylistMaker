package com.example.playlistmaker.mediateka.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FavDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addToFav(track: TrackEntity)

    @Query("DELETE FROM fav_tracks WHERE trackId = :trackId")
    suspend fun removeFromFav(trackId: Int)

    @Query("SELECT * FROM fav_tracks ORDER BY id DESC")
    fun getAllFavTracks(): Flow<List<TrackEntity>>

}