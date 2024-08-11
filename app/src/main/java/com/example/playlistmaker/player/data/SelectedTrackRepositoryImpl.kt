package com.example.playlistmaker.player.data

import com.example.playlistmaker.player.domain.SelectedTrackRepository
import com.example.playlistmaker.search.data.TrackMapper.toDomainModel
import com.example.playlistmaker.search.data.TrackMapper.toDto
import com.example.playlistmaker.search.data.dto.TrackDto
import com.example.playlistmaker.search.domain.Track
import com.google.gson.Gson

class SelectedTrackRepositoryImpl(private val gson: Gson) : SelectedTrackRepository {

    override fun decodeTrackDetails(trackJsonString: String): Track {
        try {
            val track = gson.fromJson(trackJsonString, TrackDto::class.java).toDomainModel()
            return track
        } catch (e: Exception) {
            throw e
        }
    }

    override fun encodeTrackDetails(track: Track): String {
        return gson.toJson(track.toDto())
    }
}