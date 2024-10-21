package com.example.playlistmaker.mediateka.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.playlistmaker.mediateka.data.PlaylistDbMapper

@Database(entities = [PlaylistEntity::class, TrackEntity::class], version = 2)
@TypeConverters(PlaylistDbMapper::class)
abstract class PlaylistDatabase : RoomDatabase() {
    abstract fun playlistDao(): PlaylistDao
    abstract fun favDao(): FavDao
}