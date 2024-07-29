package com.example.playlistmaker.player.domain

import com.example.playlistmaker.search.domain.Track

interface SelectedTrackRepository {
    fun decodeTrackDetails(trackJsonString: String): Track
    fun encodeTrackDetails(track: Track): String
}