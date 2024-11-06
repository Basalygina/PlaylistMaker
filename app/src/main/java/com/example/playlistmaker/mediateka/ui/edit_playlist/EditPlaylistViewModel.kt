package com.example.playlistmaker.mediateka.ui.edit_playlist

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.config.App.Companion.TAG
import com.example.playlistmaker.mediateka.data.FileManager
import com.example.playlistmaker.mediateka.domain.PlaylistInteractor
import com.example.playlistmaker.mediateka.ui.playlist_details.SelectedPlaylistState
import com.example.playlistmaker.mediateka.ui.playlists.CreatePlaylistViewModel
import kotlinx.coroutines.launch

class EditPlaylistViewModel(
    private val fileManager: FileManager,
    private val playlistInteractor: PlaylistInteractor
) : CreatePlaylistViewModel(fileManager, playlistInteractor) {
    private val _playlistState = MutableLiveData<SelectedPlaylistState>()
    val playlistState: LiveData<SelectedPlaylistState> = _playlistState

    suspend fun editPlaylist(currentPlaylistName: String, newPlaylistName: String, description: String, coverImagePath: String?) {
        playlistInteractor.editPlaylist(currentPlaylistName, newPlaylistName, description, coverImagePath)
    }

    fun getPlaylistDetails(playlistName: String?) {
        viewModelScope.launch {
            _playlistState.value = SelectedPlaylistState.Loading
            val result = runCatching {
                playlistName?.let {
                    playlistInteractor.getPlaylistDetails(it) }
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


}