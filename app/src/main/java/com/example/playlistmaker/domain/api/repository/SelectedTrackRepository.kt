package com.example.playlistmaker.domain.api.repository

import com.example.playlistmaker.domain.models.Track

interface SelectedTrackRepository {
    fun getTrackDetails(trackJsonString: String): Track
    fun dispatchTrackDetails(track: Track): String
}