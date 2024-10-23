package com.example.playlistmaker.player.ui

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.config.App.Companion.TAG
import com.example.playlistmaker.mediateka.domain.FavTracksInteractor
import com.example.playlistmaker.mediateka.domain.Playlist
import com.example.playlistmaker.mediateka.domain.PlaylistInteractor
import com.example.playlistmaker.player.domain.PlayerInteractor
import com.example.playlistmaker.search.domain.Track
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PlayerViewModel(
    private val playerInteractor: PlayerInteractor,
    private val favTracksInteractor: FavTracksInteractor,
    private val playlistInteractor: PlaylistInteractor
) : ViewModel() {
    private var timerJob: Job? = null

    private val _playerState = MutableLiveData<PlayerScreenState>()
    val playerState: LiveData<PlayerScreenState> = _playerState

    private val _trackState = MutableLiveData<SelectedTrackState>()
    val trackState: LiveData<SelectedTrackState> = _trackState

    private val _currentTime = MutableLiveData<String>()
    val currentTime: LiveData<String> = _currentTime

    private val _playlists = MutableLiveData<MutableList<Playlist>>()
    val playlists: LiveData<MutableList<Playlist>> = _playlists

    private var track: Track? = null

    fun togglePlayPause() {
        when (_playerState.value) {
            PlayerScreenState.Playing -> pause()
            else -> play()
        }
    }

    fun toggleFavorite() {
        viewModelScope.launch {
            track?.let { currentTrack ->
                if (currentTrack.isFavorite == false) {
                    favTracksInteractor.addToFav(currentTrack)
                    currentTrack.isFavorite = true
                } else {
                    favTracksInteractor.removeFromFav(currentTrack)
                    currentTrack.isFavorite = false
                }
                _trackState.postValue(SelectedTrackState.Content(currentTrack))
            }
        }
    }

    fun play() {
        // Проверка, что плеер готов перед воспроизведением
        if (_playerState.value == PlayerScreenState.Prepared || _playerState.value == PlayerScreenState.Paused) {
            playerInteractor.startPlayer()
            _playerState.value = PlayerScreenState.Playing
            updatePlayedTime()
        } else {
            Log.d(TAG, "Player state is not ready for playback: ${_playerState.value}")
        }
    }


    private fun updatePlayedTime() {
        timerJob = viewModelScope.launch {
            while (_playerState.value == PlayerScreenState.Playing) {
                delay(TIMER_STEP)
                _currentTime.value = playerInteractor.getCurrentPosition()
            }
            if (_playerState.value == PlayerScreenState.Prepared) {
                _currentTime.value = INITIAL_TIME
            }
        }
    }


    fun pause() {
        if (_playerState.value != PlayerScreenState.Paused) {
            playerInteractor.pausePlayer()
            _playerState.value = PlayerScreenState.Paused
        }
    }


    fun setCurrentTrack(trackJsonString: String) {
        viewModelScope.launch {
            _trackState.postValue(SelectedTrackState.Loading)
            try {
                track = playerInteractor.getTrackDetails(trackJsonString)
                track?.let {
                    if (favTracksInteractor.checkIfFav(it)) {
                        track!!.isFavorite = true
                    }
                    _trackState.postValue(SelectedTrackState.Content(it))
                    preparePlayer(it)
                } ?: run {
                    _trackState.postValue(SelectedTrackState.Error)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Ошибка: ${e.message}", e)
                _trackState.postValue(SelectedTrackState.Error)
            }
        }
    }

    private fun preparePlayer(track: Track) {
        playerInteractor.preparePlayer(track, onPrepared = {
            if (_playerState.value != PlayerScreenState.Prepared) {
                _playerState.postValue(PlayerScreenState.Prepared)
            }
        }, onCompletion = {
            if (_playerState.value == PlayerScreenState.Playing) {
                _playerState.postValue(PlayerScreenState.Prepared)
            }
        })
    }


    public override fun onCleared() {
        super.onCleared()
        playerInteractor.onDestroy()
    }

    fun addToPlaylist(track: Track, playlist: Playlist): Boolean {
        if (playlist.containsTrack(track.trackId)) {
            return false
        } else {
            viewModelScope.launch {
                playlistInteractor.updatePlaylist(playlist, track)
            }
            return true
        }
    }

    fun getAllPlaylists() {
        viewModelScope.launch {
            playlistInteractor.getAllPlaylists().collect { playlists ->
                val currentPlaylists = mutableListOf<Playlist>()
                currentPlaylists.addAll(playlists)
                _playlists.postValue(currentPlaylists)
            }
        }
    }

    companion object {
        private const val TIMER_STEP = 100L
        private const val INITIAL_TIME = "0:00"
    }
}
