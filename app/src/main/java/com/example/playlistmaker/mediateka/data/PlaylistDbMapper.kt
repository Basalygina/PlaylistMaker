package com.example.playlistmaker.mediateka.data

import android.util.Log
import androidx.room.TypeConverter
import com.example.playlistmaker.config.App.Companion.TAG
import com.example.playlistmaker.mediateka.data.PlaylistDbMapper.toPlaylistEntity
import com.example.playlistmaker.mediateka.data.db.PlaylistEntity
import com.example.playlistmaker.mediateka.domain.Playlist

object PlaylistDbMapper {
    @TypeConverter
    fun fromTracksList(tracks: List<Int>): String {
        return tracks.joinToString(separator = ",")
    }

    @TypeConverter
    fun toTracksList(tracksString: String): List<Int> {
        var trackList: List<Int> = emptyList()
        if (tracksString.isNotEmpty()) {
            trackList = tracksString.split(",").map { it.toInt() }
        }
        return trackList
    }

    @TypeConverter
    fun Playlist.toPlaylistEntity(): PlaylistEntity {
        return PlaylistEntity(
            this.playlistName,
            this.description,
            this.cover,
            this.tracks,
            this.tracksCount,
            this.durationSum
        )
    }

    @TypeConverter
    fun PlaylistEntity.toPlaylist(): Playlist {
        return Playlist(
            this.playlistName,
            this.description,
            this.cover,
            this.tracks,
            this.tracksCount,
            this.durationSum
        )
    }
}