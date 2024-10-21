package com.example.playlistmaker.mediateka.ui.playlists

import com.example.playlistmaker.mediateka.domain.Playlist


sealed class PlaylistsScreenState {
    object Empty: PlaylistsScreenState()
    data class Playlists(val playlists: List<Playlist>): PlaylistsScreenState()
    data class NavigateToPlaylistDetails(val playlistName: String, val navigationHandled: Boolean = false) : PlaylistsScreenState()
}