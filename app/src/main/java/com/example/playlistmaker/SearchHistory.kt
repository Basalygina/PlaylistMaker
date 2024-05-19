package com.example.playlistmaker

import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException

class SearchHistory(private val sharedPref: SharedPreferences) {
    private val gson = Gson()
    var searchHistoryList: MutableList<Track> = mutableListOf()
    init {
        searchHistoryList = getSearchHistory()
    }

    fun addToSearchHistory(selectedTrack: Track) {
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

    fun getSearchHistory(): MutableList<Track> {
        var json = sharedPref.getString(App.SEARCH_HISTORY_KEY, null)
        if (json != null) {
            searchHistoryList.clear()
            searchHistoryList.addAll(parseJson(json))
        }
        return searchHistoryList
    }

    fun saveSearchHistory() {
        val json = gson.toJson(searchHistoryList)
        sharedPref.edit()
            .putString(App.SEARCH_HISTORY_KEY, json)
            .apply()
    }

    private fun parseJson(jsonString: String): MutableList<Track> {
        return try {
            // Если JSON это массив
            val tracks = gson.fromJson(jsonString, Array<Track>::class.java).toMutableList()
            tracks
        } catch (e: JsonSyntaxException) {
            // Если JSON это объект
            val track = gson.fromJson(jsonString, Track::class.java)
            mutableListOf(track)
        }

    }

    companion object {
        const val SEARCH_HISTORY_MAX_SIZE = 10
    }
}