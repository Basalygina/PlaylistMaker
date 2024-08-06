package com.example.playlistmaker.player.data

import com.example.playlistmaker.search.domain.Track

interface PlayerHandler {
    fun preparePlayer(track: Track, onPrepared: () -> Unit, onCompletion: () -> Unit)
    fun play()
    fun pause()
    fun getCurrentPosition(): String
    fun shutdownPlayer()
}