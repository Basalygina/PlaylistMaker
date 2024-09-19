package com.example.playlistmaker.search.ui

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.config.App.Companion.TAG
import com.example.playlistmaker.search.domain.Track
import com.example.playlistmaker.search.domain.TracksInteractor
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SearchViewModel(
    private val tracksInteractor: TracksInteractor
) : ViewModel() {

    private val tracks = mutableListOf<Track>()
    private val searchHistory = mutableListOf<Track>()

    private val _searchScreenState = MutableLiveData<SearchScreenState>()
    val searchScreenState: LiveData<SearchScreenState> = _searchScreenState

    private val _previousSearchScreenState = MutableLiveData<SearchScreenState>()
    private var searchJob: Job? = null
    private var latestSearchText: String? = null

    init {
        searchHistory.clear()
        searchHistory.addAll(tracksInteractor.getSearchHistory())
        if (searchHistory.isNotEmpty()) {
            updateSearchScreenState(SearchScreenState.SearchHistory(searchHistory))
        } else {
            updateSearchScreenState(SearchScreenState.Prepared)
        }
    }

    fun searchDebounce(changedText: String) {
        if (latestSearchText == changedText) {
            return
        }

        this.latestSearchText = changedText
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(SEARCH_DEBOUNCE_DELAY)
            searchRequest(changedText)
        }
    }

    private fun searchRequest(query: String) {
        if (query.isNotEmpty()) {
            updateSearchScreenState(SearchScreenState.Loading)
            tracks.clear()
            viewModelScope.launch {
                try {
                    tracksInteractor
                        .searchTracks(query)
                        .collect { foundTracks ->
                            if (foundTracks.isNotEmpty()) {
                                tracks.addAll(foundTracks)
                                updateSearchScreenState(SearchScreenState.Results(tracks))
                            } else {
                                updateSearchScreenState(SearchScreenState.NothingFound)
                            }
                        }
                } catch (t: Throwable) {
                    Log.e(TAG, "Ошибка: ${t.message}")
                    updateSearchScreenState(SearchScreenState.Error)
                }
            }
        }

    }


    private fun updateSearchScreenState(state: SearchScreenState) {
        _previousSearchScreenState.postValue(_searchScreenState.value)
        _searchScreenState.postValue(state)
    }


    fun clearSearchHistory() {
        tracksInteractor.clearSearchHistory()
        updateSearchScreenState(SearchScreenState.Prepared)
    }

    fun getSearchHistory() {
        searchHistory.clear()
        searchHistory.addAll(tracksInteractor.getSearchHistory())
        if (searchHistory.isNotEmpty()) {
            updateSearchScreenState(SearchScreenState.SearchHistory(searchHistory))
        } else {
            updateSearchScreenState(SearchScreenState.Prepared)
        }
    }

    fun onTrackSelected(track: Track) {


        val trackJsonString = tracksInteractor.encodeTrackDetails(track)
        tracksInteractor.addToSearchHistory(track)
        searchHistory.clear()
        searchHistory.addAll(tracksInteractor.getSearchHistory())
        if (_previousSearchScreenState.value is SearchScreenState.SearchHistory || _searchScreenState.value is SearchScreenState.SearchHistory) {
            updateSearchScreenState(SearchScreenState.SearchHistory(searchHistory))
        }

        viewModelScope.launch {
            delay(NAVIGATE_TO_PLAYER_DELAY)
            updateSearchScreenState(SearchScreenState.NavigateToPlayer(trackJsonString))
        }
    }

    fun stopDelayedSearchRequest() {
        searchJob?.cancel()
    }


    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 1_000L
        private const val SEARCH_DEBOUNCE_DELAY = 2_000L
        private const val NAVIGATE_TO_PLAYER_DELAY = 200L
    }

}

