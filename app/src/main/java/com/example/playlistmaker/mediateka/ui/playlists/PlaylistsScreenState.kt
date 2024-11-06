package com.example.playlistmaker.mediateka.ui.playlists

import com.example.playlistmaker.mediateka.domain.Playlist


sealed class PlaylistsScreenState {
    object Empty: PlaylistsScreenState()
    data class Playlists(val playlists: List<Playlist>): PlaylistsScreenState()
}