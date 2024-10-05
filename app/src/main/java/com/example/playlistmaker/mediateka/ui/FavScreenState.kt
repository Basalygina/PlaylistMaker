package com.example.playlistmaker.mediateka.ui

import com.example.playlistmaker.search.domain.Track

sealed class FavScreenState {
    object NothingInFav: FavScreenState()
    data class FavTracks(val favTracks: List<Track>): FavScreenState()
    data class NavigateToPlayer(val trackJsonString: String, val navigationHandled: Boolean = false) : FavScreenState()
}