package com.example.playlistmaker.search.data

import com.example.playlistmaker.search.data.dto.TrackDto
import com.example.playlistmaker.search.domain.Track
import java.util.Locale

object TrackMapper {
    fun TrackDto.toDomainModel(): Track {
        return Track(
            trackId ?: Track.TRACK_DEFAULT_INT_VALUE,
            trackName ?: Track.TRACK_DEFAULT_STRING_VALUE,
            artistName ?: Track.TRACK_DEFAULT_STRING_VALUE,
            convertMillisToString(trackTimeMillis ?: Track.TRACK_DEFAULT_INT_VALUE),
            artworkUrl100 ?: Track.TRACK_DEFAULT_STRING_VALUE,
            collectionName ?: Track.TRACK_DEFAULT_STRING_VALUE,
            releaseDate ?: Track.TRACK_DEFAULT_STRING_VALUE,
            primaryGenreName ?: Track.TRACK_DEFAULT_STRING_VALUE,
            country ?: Track.TRACK_DEFAULT_STRING_VALUE,
            previewUrl ?: Track.TRACK_DEFAULT_STRING_VALUE
        )
    }

    fun Track.toDto(): TrackDto {
        return TrackDto(
            trackId,
            trackName,
            artistName,
            convertStringToMillis(trackTimeString),
            artworkUrl100,
            collectionName,
            releaseDate,
            primaryGenreName,
            country,
            previewUrl
        )
    }

    private fun convertStringToMillis(time: String): Int {
        val parts = time.split(":")
        if (parts.size != 2) {
            throw IllegalArgumentException("Invalid time format, expected MM:SS")
        }
        val minutes = parts[0].toIntOrNull() ?: throw IllegalArgumentException("Invalid minutes value")
        val seconds = parts[1].toIntOrNull() ?: throw IllegalArgumentException("Invalid seconds value")
        return (minutes * 60 + seconds) * 1000
    }

    private fun convertMillisToString(millis: Int): String {
        val minutes = (millis / 1000) / 60
        val seconds = (millis / 1000) % 60
        return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
    }
}