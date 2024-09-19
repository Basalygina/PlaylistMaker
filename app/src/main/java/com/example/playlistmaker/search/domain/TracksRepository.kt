package com.example.playlistmaker.search.domain

import kotlinx.coroutines.flow.Flow

interface TracksRepository {
    fun searchTracks (expression: String): Flow<MutableList<Track>>
}