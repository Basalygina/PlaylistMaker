package com.example.playlistmaker.domain.api.interactors

import com.example.playlistmaker.domain.models.Track


interface TracksInteractor {
    fun searchTracks (expression: String, consumer: TracksConsumer)

    interface TracksConsumer{
        fun consume(foundTracks: List<Track>)
        fun onError(t: Throwable)
    }


}