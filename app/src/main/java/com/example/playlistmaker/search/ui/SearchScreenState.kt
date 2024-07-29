package com.example.playlistmaker.search.ui

import com.example.playlistmaker.search.domain.Track

sealed class SearchScreenState {
    object Loading: SearchScreenState()
    object Prepared: SearchScreenState()
    object NothingFound: SearchScreenState()
    object Error: SearchScreenState()
    data class Results(val resultsList: List<Track>): SearchScreenState()
    data class SearchHistory(val searchHistoryList: List<Track>): SearchScreenState()
    data class NavigateToPlayer(val trackJsonString: String) : SearchScreenState()
}