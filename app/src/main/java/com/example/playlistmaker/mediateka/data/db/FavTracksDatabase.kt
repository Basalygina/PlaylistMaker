package com.example.playlistmaker.mediateka.data.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(version = 1, entities = [TrackEntity::class])
abstract class FavTracksDatabase: RoomDatabase() {

    abstract fun favDao(): FavDao
}