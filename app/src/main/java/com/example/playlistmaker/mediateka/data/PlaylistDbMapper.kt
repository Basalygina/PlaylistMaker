package com.example.playlistmaker.mediateka.data

import androidx.room.TypeConverter
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

    fun Playlist.toPlaylistEntity(): PlaylistEntity {
        return PlaylistEntity(
            this.playlistName,
            this.description,
            this.cover,
            this.tracks,
            this.tracksCount,
        )
    }

    fun PlaylistEntity.toPlaylist(): Playlist {
        return Playlist(
            this.playlistName,
            this.description,
            this.cover,
            this.tracks,
            this.tracksCount
        )
    }
}