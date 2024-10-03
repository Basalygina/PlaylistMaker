package com.example.playlistmaker.mediateka.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.mediateka.domain.FavTracksInteractor
import com.example.playlistmaker.search.domain.Track
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class FavoriteViewModel(private val favTracksInteractor: FavTracksInteractor) : ViewModel() {
    private val _favScreenState = MutableLiveData<FavScreenState>()
    val favScreenState: LiveData<FavScreenState> = _favScreenState

    fun getFavTracks() {
        viewModelScope.launch {
            favTracksInteractor.getAllFavTracks().collect { tracks ->
                if (tracks.isEmpty()) updateSearchScreenState(FavScreenState.NothingInFav)
                else updateSearchScreenState(
                    FavScreenState.FavTracks(tracks)
                )
            }
        }
    }

    fun onTrackSelected(track: Track) {
        viewModelScope.launch {
            val trackJsonString = favTracksInteractor.encodeTrackDetails(track)
            updateSearchScreenState(FavScreenState.NavigateToPlayer(trackJsonString))
            delay(CLICK_DEBOUNCE_DELAY)
            updateSearchScreenState(FavScreenState.NavigateToPlayer(trackJsonString, true))
        }
    }

    private fun updateSearchScreenState(state: FavScreenState) {
        viewModelScope.launch {
            _favScreenState.postValue(state)
        }

    }

    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 1_000L
    }

}