package com.example.playlistmaker.data.repository

import com.example.playlistmaker.domain.api.repository.SearchHistoryRepository
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.ui.App
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import android.content.SharedPreferences
import com.example.playlistmaker.data.TrackMapper.toDomainModel
import com.example.playlistmaker.data.TrackMapper.toDto
import com.example.playlistmaker.data.dto.TrackDto

class SearchHistoryRepositoryImpl(private val sharedPref: SharedPreferences): SearchHistoryRepository {
    private val gson = Gson()

    var searchHistoryList: MutableList<Track> = mutableListOf()
    init {
        searchHistoryList = getSearchHistory()
    }

    override fun getSearchHistory(): MutableList<Track> {
        var json = sharedPref.getString(App.SEARCH_HISTORY_KEY, null)
        if (json != null) {
            searchHistoryList.clear()
            searchHistoryList.addAll(parseJson(json))
        }
        return searchHistoryList
    }

    override fun addToSearchHistory(selectedTrack: Track) {
        getSearchHistory()
        if (searchHistoryList.contains(selectedTrack)) {
            searchHistoryList.remove(selectedTrack)
        } else {
            if (searchHistoryList.size == SEARCH_HISTORY_MAX_SIZE) {
                searchHistoryList.removeAt(SEARCH_HISTORY_MAX_SIZE - 1)
            }
        }
        searchHistoryList.add(0, selectedTrack)
        saveSearchHistory()
    }

    override fun clearSearchHistory() {
        searchHistoryList.clear()
        saveSearchHistory()
    }

    override fun saveSearchHistory() {
        val json = gson.toJson(searchHistoryList.map { it.toDto() })
        sharedPref.edit()
            .putString(App.SEARCH_HISTORY_KEY, json)
            .apply()
    }

    private fun parseJson(jsonString: String): MutableList<Track> {
        return try {
            // Если JSON это массив
            val tracksDto = gson.fromJson(jsonString, Array<TrackDto>::class.java)
            tracksDto.map { it.toDomainModel() }.toMutableList()
        } catch (e: JsonSyntaxException) {
            // Если JSON это объект
            val track = gson.fromJson(jsonString, TrackDto::class.java).toDomainModel()
            mutableListOf(track)
        }

    }

    companion object {
        const val SEARCH_HISTORY_MAX_SIZE = 10
    }
}