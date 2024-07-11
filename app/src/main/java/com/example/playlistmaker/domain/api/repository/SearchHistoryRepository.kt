package com.example.playlistmaker.domain.api.repository

import com.example.playlistmaker.domain.models.Track

interface SearchHistoryRepository {
    fun getSearchHistory(): List<Track>
    fun addToSearchHistory(selectedTrack: Track)
    fun clearSearchHistory()
    fun saveSearchHistory()
}