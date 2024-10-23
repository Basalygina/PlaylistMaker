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
import kotlinx.coroutines.yield

class SearchViewModel(
    private val tracksInteractor: TracksInteractor
) : ViewModel() {

    private val tracks = mutableListOf<Track>()
    private val searchHistory = mutableListOf<Track>()

    private val _searchScreenState = MutableLiveData<SearchScreenState>()
    val searchScreenState: LiveData<SearchScreenState> = _searchScreenState
    private var modeSearchHistory = true
    private var searchJob: Job? = null
    private var latestSearchText: String? = null

    init {
        showInitialSearchHistory()
    }

    private fun showInitialSearchHistory() {
        viewModelScope.launch {
            searchHistory.clear()
            searchHistory.addAll(tracksInteractor.getSearchHistory())
        }
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
            modeSearchHistory = false
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
        if (state is SearchScreenState.SearchHistory) {
            modeSearchHistory = true
        }
        viewModelScope.launch {
            _searchScreenState.postValue(state)
        }

    }


    fun clearSearchHistory() {
        viewModelScope.launch {
            tracksInteractor.clearSearchHistory()
            updateSearchScreenState(SearchScreenState.Prepared)
        }
    }

    fun getSearchHistory() {
        viewModelScope.launch {
            searchHistory.clear()
            searchHistory.addAll(tracksInteractor.getSearchHistory())
            if (searchHistory.isNotEmpty()) {
                updateSearchScreenState(SearchScreenState.SearchHistory(searchHistory))
            } else {
                updateSearchScreenState(SearchScreenState.Prepared)
            }
        }
    }

    fun onTrackSelected(track: Track) {
        viewModelScope.launch {
            val trackJsonString = tracksInteractor.encodeTrackDetails(track)
            val reorderHistoryJob = launch {
                tracksInteractor.addToSearchHistory(track)
                searchHistory.clear()
                searchHistory.addAll(tracksInteractor.getSearchHistory())
                if (modeSearchHistory) {
                    updateSearchScreenState(SearchScreenState.SearchHistory(searchHistory))
                }
            }
            reorderHistoryJob.join()
            yield()
            updateSearchScreenState(SearchScreenState.NavigateToPlayer(trackJsonString))
            delay(CLICK_DEBOUNCE_DELAY)
            updateSearchScreenState(SearchScreenState.NavigateToPlayer(trackJsonString, true))
        }
    }

    fun stopDelayedSearchRequest() {
        searchJob?.cancel()
    }

    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 100L
        private const val SEARCH_DEBOUNCE_DELAY = 2_000L
    }

}