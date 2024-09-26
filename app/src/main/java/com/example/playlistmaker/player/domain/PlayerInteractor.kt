package com.example.playlistmaker.player.domain

import com.example.playlistmaker.search.domain.Track

interface PlayerInteractor {
    fun getTrackDetails(trackJsonString: String, consumer: TrackConsumer)

    interface TrackConsumer {
        fun consume(track: Track)
        fun onError(t: Throwable)
    }

    fun preparePlayer(track: Track, onPrepared: () -> Unit, onCompletion: () -> Unit)
    fun startPlayer()
    fun pausePlayer()
    fun getCurrentPosition(): String
    fun onDestroy()

}