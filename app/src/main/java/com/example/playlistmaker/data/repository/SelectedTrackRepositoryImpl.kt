package com.example.playlistmaker.data.repository

import com.example.playlistmaker.domain.api.repository.SelectedTrackRepository
import com.example.playlistmaker.domain.models.Track
import com.google.gson.Gson

class SelectedTrackRepositoryImpl: SelectedTrackRepository {
    private val gson = Gson()

    override fun getTrackDetails(trackJsonString: String): Track {
        return gson.fromJson(trackJsonString, Track::class.java)
    }

    override fun dispatchTrackDetails(track: Track): String {
        return gson.toJson(track)
    }
}