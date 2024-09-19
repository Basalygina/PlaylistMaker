package com.example.playlistmaker.search.domain

import com.example.playlistmaker.player.domain.SelectedTrackRepository
import kotlinx.coroutines.flow.Flow
import java.util.concurrent.Executor

class TracksInteractorImpl(
    private val repository: TracksRepository,
    private val searchHistoryRepository: SearchHistoryRepository,
    private val selectedTrackRepository: SelectedTrackRepository
): TracksInteractor {

    override fun searchTracks(expression: String): Flow<MutableList<Track>> =
        repository.searchTracks(expression)


    override fun getSearchHistory() =
        searchHistoryRepository.getSearchHistory()

    override fun clearSearchHistory() {
        searchHistoryRepository.clearSearchHistory()
    }

    override fun addToSearchHistory(track: Track) {
        searchHistoryRepository.addToSearchHistory(track)
    }

    override fun encodeTrackDetails(track: Track) =
        selectedTrackRepository.encodeTrackDetails(track)


}