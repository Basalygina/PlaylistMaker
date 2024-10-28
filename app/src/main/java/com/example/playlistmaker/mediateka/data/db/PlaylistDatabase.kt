package com.example.playlistmaker.mediateka.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.playlistmaker.mediateka.data.PlaylistDbMapper

@Database(entities = [PlaylistEntity::class, TrackEntity::class, TrackInPlaylistEntity::class], version = 1)
@TypeConverters(PlaylistDbMapper::class)
abstract class PlaylistDatabase : RoomDatabase() {
    abstract fun daoPlaylist(): DaoPlaylist
    abstract fun daoFav(): DaoFav
    abstract fun daoTrackInPlaylist(): DaoTrackInPlaylist
}