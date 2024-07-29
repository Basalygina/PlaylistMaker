package com.example.playlistmaker.search.domain


interface TracksInteractor {
    fun searchTracks (expression: String, consumer: TracksConsumer)

    interface TracksConsumer{
        fun consume(foundTracks: List<Track>)
        fun onError(t: Throwable)
    }


}