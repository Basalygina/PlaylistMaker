package com.example.playlistmaker.data.handler

import com.example.playlistmaker.domain.api.interactors.PlayerInteractor
import com.example.playlistmaker.domain.models.Track

interface PlayerHandler {
    fun preparePlayer(track: Track, onPrepared: () -> Unit, onCompletion: () -> Unit)
    fun play()
    fun pause()
    fun getCurrentPosition(): String
    fun shutdownPlayer()
}