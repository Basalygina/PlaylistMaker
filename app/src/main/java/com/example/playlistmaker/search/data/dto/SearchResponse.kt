package com.example.playlistmaker.search.data.dto

class SearchResponse(
    val resultCount: Int,
    val results: ArrayList<TrackDto>
) : Response() {
}