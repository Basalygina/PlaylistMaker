package com.example.playlistmaker.data.dto

import com.example.playlistmaker.domain.models.Track

class SearchResponse(
    val resultCount: Int,
    val results: ArrayList<TrackDto>
) : Response() {
}