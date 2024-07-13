package com.example.playlistmaker.data.repository

import com.example.playlistmaker.data.TrackMapper.toDomainModel
import com.example.playlistmaker.data.TrackMapper.toDto
import com.example.playlistmaker.domain.api.repository.SelectedTrackRepository
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.data.dto.TrackDto
import com.google.gson.Gson

class SelectedTrackRepositoryImpl: SelectedTrackRepository {
    private val gson = Gson()

    override fun decodeTrackDetails(trackJsonString: String): Track {
        return gson.fromJson(trackJsonString, TrackDto::class.java).toDomainModel()
    }

    override fun encodeTrackDetails(track: Track): String {
        return gson.toJson(track.toDto())
    }
}