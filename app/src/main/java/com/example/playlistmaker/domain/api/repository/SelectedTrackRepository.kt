package com.example.playlistmaker.domain.api.repository

import com.example.playlistmaker.domain.models.Track

interface SelectedTrackRepository {
    fun decodeTrackDetails(trackJsonString: String): Track
    fun encodeTrackDetails(track: Track): String
}