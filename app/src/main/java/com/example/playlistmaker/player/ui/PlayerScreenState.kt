package com.example.playlistmaker.player.ui

import com.example.playlistmaker.search.domain.Track

sealed class PlayerScreenState {
    object Prepared: PlayerScreenState()
    object Playing: PlayerScreenState()
    object Paused: PlayerScreenState()
}