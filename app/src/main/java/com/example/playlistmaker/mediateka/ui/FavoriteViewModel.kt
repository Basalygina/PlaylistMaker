package com.example.playlistmaker.mediateka.ui

import android.util.Log
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
            Log.d("PMtest", "..getFavTracks()")
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
            Log.d("PMtest", "..onTrackSelected()")
            val trackJsonString = favTracksInteractor.encodeTrackDetails(track)
            updateSearchScreenState(FavScreenState.NavigateToPlayer(trackJsonString))
            delay(CLICK_DEBOUNCE_DELAY)
            updateSearchScreenState(FavScreenState.NavigateToPlayer(trackJsonString, true))
        }
    }

    private fun updateSearchScreenState(state: FavScreenState) {
        viewModelScope.launch {
            _favScreenState.postValue(state)
            Log.d("PMtest", "!!new state will be ${_favScreenState.value.toString()}")
        }

    }

    fun postLog() {
        viewModelScope.launch {
            while (true) {
                Log.d("PMtest", "state is ${_favScreenState.value.toString()}")
                delay(5000)
            }
        }


    }

    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 1_000L
    }

}