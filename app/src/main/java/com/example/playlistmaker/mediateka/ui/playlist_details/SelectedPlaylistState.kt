package com.example.playlistmaker.mediateka.ui.playlist_details

import com.example.playlistmaker.mediateka.domain.Playlist
import com.example.playlistmaker.search.domain.Track

sealed class SelectedPlaylistState {
    object Loading : SelectedPlaylistState()
    data class Content(
        val playlistModel: Playlist,
        val trackList: List<Track>
    ) : SelectedPlaylistState()

    data class NavigateToPlayer(
        val trackJsonString: String,
        val navigationHandled: Boolean = false
    ) : SelectedPlaylistState()

    object Error : SelectedPlaylistState()

    object Deleted: SelectedPlaylistState()

    data class SharePlaylist(
        val playlistString: String,
        val isSharingPossible: Boolean
    ) : SelectedPlaylistState()

}