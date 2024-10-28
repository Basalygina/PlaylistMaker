package com.example.playlistmaker.mediateka.ui.playlist_details

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.config.App.Companion.TAG
import com.example.playlistmaker.mediateka.domain.PlaylistInteractor
import com.example.playlistmaker.mediateka.ui.MediaTextFormatter
import com.example.playlistmaker.search.domain.Track
import com.example.playlistmaker.search.domain.TracksInteractor
import com.example.playlistmaker.search.ui.SearchViewModel.Companion.CLICK_DEBOUNCE_DELAY
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PlaylistDetailsViewModel(
    private val playlistInteractor: PlaylistInteractor,
    private val tracksInteractor: TracksInteractor
) : ViewModel() {
    private val _playlistState = MutableLiveData<SelectedPlaylistState>()
    val playlistState: LiveData<SelectedPlaylistState> = _playlistState

    fun setCurrentPlaylist(currentPlaylistName: String?) {
        viewModelScope.launch {
            _playlistState.value = SelectedPlaylistState.Loading
            val result = runCatching {
                currentPlaylistName?.let { playlistInteractor.getPlaylistDetails(it) }
            }
            result.onSuccess { pair ->
                pair?.let { (playlist, tracks) ->
                    _playlistState.value = SelectedPlaylistState.Content(playlist, tracks)
                }
            }.onFailure { e ->
                Log.e(TAG, "Ошибка: ${e.message}", e)
                _playlistState.value = SelectedPlaylistState.Error
            }
        }
    }

    fun onTrackSelected(track: Track) {
        viewModelScope.launch {
            val trackJsonString = tracksInteractor.encodeTrackDetails(track)
            _playlistState.value = SelectedPlaylistState.NavigateToPlayer(trackJsonString)
            delay(CLICK_DEBOUNCE_DELAY)
            _playlistState.value = SelectedPlaylistState.NavigateToPlayer(trackJsonString, true)
        }
    }

    fun deleteTrackFromPlaylist(playlistName: String, track: Track) {
        viewModelScope.launch {
            playlistInteractor.updateTracksInPlaylist(playlistName, track, isAdding = false)
            val updatedPlaylist = playlistInteractor.getPlaylistDetails(playlistName)
            _playlistState.value =
                SelectedPlaylistState.Content(updatedPlaylist.first, updatedPlaylist.second)
        }
    }

    fun deletePlaylist(playlistName: String) {
        viewModelScope.launch {
            playlistInteractor.deletePlaylist(playlistName)
            _playlistState.value = SelectedPlaylistState.Deleted
        }
    }

    fun sharePlaylist(playlistName: String) {
        viewModelScope.launch {
            val (playlist, trackList) = playlistInteractor.getPlaylistDetails(playlistName)
            val playlistString = buildString {
                appendLine(playlist.playlistName)
                appendLine(playlist.description)
                appendLine("${playlist.tracksCount} ${MediaTextFormatter.getTrackDeclension(playlist.tracksCount)}")

                trackList.forEachIndexed { index, track ->
                    appendLine("${index + 1}. ${track.artistName} - ${track.trackName} (${track.trackTimeString})")
                }
            }
            val isSharingPossible = (trackList.size > 0)
            _playlistState.value = SelectedPlaylistState.SharePlaylist(playlistString, isSharingPossible)
        }
    }
}
