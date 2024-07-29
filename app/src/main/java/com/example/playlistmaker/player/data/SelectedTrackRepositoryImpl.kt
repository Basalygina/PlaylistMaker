package com.example.playlistmaker.player.data

import android.util.Log
import com.example.playlistmaker.config.App.Companion.TAG
import com.example.playlistmaker.search.data.TrackMapper.toDomainModel
import com.example.playlistmaker.search.data.TrackMapper.toDto
import com.example.playlistmaker.player.domain.SelectedTrackRepository
import com.example.playlistmaker.search.domain.Track
import com.example.playlistmaker.search.data.dto.TrackDto
import com.google.gson.Gson

class SelectedTrackRepositoryImpl: SelectedTrackRepository {
    private val gson = Gson()

    override fun decodeTrackDetails(trackJsonString: String): Track {
        try {
            Log.d(TAG, "Decoding track details from JSON: $trackJsonString")
            val track = gson.fromJson(trackJsonString, TrackDto::class.java).toDomainModel()
            Log.d(TAG, "Decoded track: ${track.trackName}")
            return track
        } catch (e: Exception) {
            Log.e(TAG, "Error decoding track details from JSON", e)
            throw e
        }
    }

    override fun encodeTrackDetails(track: Track): String {
        return gson.toJson(track.toDto())
    }
}