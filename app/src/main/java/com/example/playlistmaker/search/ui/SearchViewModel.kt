package com.example.playlistmaker.search.ui

import android.app.Application
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlistmaker.config.App
import com.example.playlistmaker.config.App.Companion.TAG
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.search.domain.Track
import com.example.playlistmaker.search.domain.TracksInteractor

class SearchViewModel(private val tracksInteractor: TracksInteractor): ViewModel() {
    private val handler = Handler(Looper.getMainLooper())
    val tracks = mutableListOf<Track>()
    val searchHistory = mutableListOf<Track>()

    private val _searchScreenState = MutableLiveData<SearchScreenState>()
    val searchScreenState: LiveData<SearchScreenState> = _searchScreenState

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
        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)
        val searchRunnable = Runnable { searchRequest(changedText) }
        val postTime = SystemClock.uptimeMillis() + SEARCH_DEBOUNCE_DELAY
        handler.postAtTime(
            searchRunnable,
            SEARCH_REQUEST_TOKEN,
            postTime
        )
    }

    private fun searchRequest(query: String) {
        if (query.isNotEmpty()) {
            updateSearchScreenState(SearchScreenState.Loading)
            tracksInteractor.searchTracks(
                query,
                object : TracksInteractor.TracksConsumer {
                    override fun consume(foundTracks: List<Track>) {
                        tracks.clear()
                        if (foundTracks.isNotEmpty()) {
                            tracks.addAll(foundTracks)
                            updateSearchScreenState(SearchScreenState.Results(tracks))
                        } else {
                            updateSearchScreenState(SearchScreenState.NothingFound)
                        }

                    }

                    override fun onError(t: Throwable) {
                        Log.e(TAG, "Ошибка: ${t.message}")
                        tracks.clear()
                        updateSearchScreenState(SearchScreenState.Error)
                    }
                }
            )
        }

    }


    private fun updateSearchScreenState(state: SearchScreenState) {
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
        if (searchHistory.isNotEmpty()) {
            updateSearchScreenState(SearchScreenState.SearchHistory(searchHistory))
        } else {
            updateSearchScreenState(SearchScreenState.Prepared)
        }
        updateSearchScreenState(SearchScreenState.NavigateToPlayer(trackJsonString))
    }

    fun stopDelayedSearchRequest() {
        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)
    }


    companion object {
        fun getViewModelFactory(context: Context): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                SearchViewModel(Creator.provideTracksInteractor(context))
            }
        }

        private const val CLICK_DEBOUNCE_DELAY = 1_000L
        private const val SEARCH_DEBOUNCE_DELAY = 2_000L
        private val SEARCH_REQUEST_TOKEN = Any()
    }

}

