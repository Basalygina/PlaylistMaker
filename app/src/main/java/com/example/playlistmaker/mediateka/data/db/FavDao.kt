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

    @Delete
    suspend fun removeFromFav(track: TrackEntity)

    @Query("SELECT * FROM fav_tracks ORDER BY id DESC")
    fun getAllFavTracks(): Flow<List<TrackEntity>>

}