package com.example.playlistmaker.mediateka.data.db

import androidx.room.Insert
import androidx.room.OnConflictStrategy

interface TrackInPlaylistDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addTrackInPlaylist(trackInPlaylistEntity: TrackInPlaylistEntity)
}