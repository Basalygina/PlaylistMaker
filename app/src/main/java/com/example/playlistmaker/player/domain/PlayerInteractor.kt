package com.example.playlistmaker.player.domain

import com.example.playlistmaker.search.domain.Track

interface PlayerInteractor {
    suspend fun getTrackDetails(trackJsonString: String) : Track
    fun preparePlayer(track: Track, onPrepared: () -> Unit, onCompletion: () -> Unit)
    fun startPlayer()
    fun pausePlayer()
    fun getCurrentPosition(): String
    fun onDestroy()

}