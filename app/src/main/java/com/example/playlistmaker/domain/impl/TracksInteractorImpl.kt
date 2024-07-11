package com.example.playlistmaker.domain.impl

import com.example.playlistmaker.domain.api.interactors.TracksInteractor
import com.example.playlistmaker.domain.api.repository.TracksRepository
import java.util.concurrent.Executors

class TracksInteractorImpl(private val repository: TracksRepository): TracksInteractor {
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
}