package com.example.playlistmaker.search.domain

import com.example.playlistmaker.player.domain.SelectedTrackRepository
import java.util.concurrent.Executors

class TracksInteractorImpl(
    private val repository: TracksRepository,
    private val searchHistoryRepository: SearchHistoryRepository,
    private val selectedTrackRepository: SelectedTrackRepository
): TracksInteractor {
    private val executor = Executors.newCachedThreadPool()

    override fun searchTracks(expression: String, consumer: TracksInteractor.TracksConsumer) {
        executor.execute {
            try {
                consumer.consume(repository.searchTracks(expression))
            } catch (t: Throwable) {
                consumer.onError(t)
            }
        }
    }

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