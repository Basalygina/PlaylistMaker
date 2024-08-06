package com.example.playlistmaker.search.domain

interface SearchHistoryRepository {
    fun getSearchHistory(): MutableList<Track>
    fun addToSearchHistory(selectedTrack: Track)
    fun clearSearchHistory()
    fun saveSearchHistory()
}