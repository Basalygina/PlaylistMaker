package com.example.playlistmaker.player.ui

import com.example.playlistmaker.search.domain.Track

sealed class SelectedTrackState {
    object Loading: SelectedTrackState()
    data class Content(
        val trackModel: Track,
    ): SelectedTrackState()
    object Error: SelectedTrackState()
}