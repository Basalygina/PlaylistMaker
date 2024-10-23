package com.example.playlistmaker.mediateka.ui.playlists

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.mediateka.domain.PlaylistInteractor
import com.example.playlistmaker.mediateka.ui.favorite.FavScreenState
import com.example.playlistmaker.search.domain.Track
import com.example.playlistmaker.search.ui.SearchScreenState
import kotlinx.coroutines.launch

class PlaylistsViewModel(
    private val playlistInteractor: PlaylistInteractor
) : ViewModel() {
    private val _playlistScreenState = MutableLiveData<PlaylistsScreenState>()
    val playlistScreenState: LiveData<PlaylistsScreenState> = _playlistScreenState

    fun getPlaylists() {
        viewModelScope.launch {
            playlistInteractor.getAllPlaylists().collect{playlists ->
                if (playlists.isEmpty()) updateSearchScreenState(PlaylistsScreenState.Empty)
                else updateSearchScreenState(
                    PlaylistsScreenState.Playlists(playlists)
                )
            }
        }
        }




private fun updateSearchScreenState(state: PlaylistsScreenState) {
    viewModelScope.launch {
        _playlistScreenState.postValue(state)
    }

}


}