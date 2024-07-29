package com.example.playlistmaker.search.domain

interface TracksRepository {
    fun searchTracks (expression: String): MutableList<Track>
}