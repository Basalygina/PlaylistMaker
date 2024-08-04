package com.example.playlistmaker.player.ui

import android.app.Application
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.player.domain.PlayerInteractor
import com.example.playlistmaker.player.domain.SelectedTrackRepository
import com.example.playlistmaker.search.domain.Track
import com.example.playlistmaker.config.App.Companion.TAG

class PlayerViewModel(
    private val playerInteractor: PlayerInteractor
) : ViewModel() {

    private val mainHandler = Handler(Looper.getMainLooper())
    private var timerRunnable: Runnable? = null

    private val _playerState = MutableLiveData<PlayerScreenState>()
    val playerState: LiveData<PlayerScreenState> = _playerState

    private val _trackState = MutableLiveData<SelectedTrackState>()
    val trackState: LiveData<SelectedTrackState> = _trackState

    private val _currentTime = MutableLiveData<String>()
    val currentTime: LiveData<String> = _currentTime

    fun togglePlayPause(){
        when (_playerState.value) {
            PlayerScreenState.Playing -> pause()
            else -> play()
        }
    }
    fun play() {
        if (_playerState.value != PlayerScreenState.Playing) {
            playerInteractor.startPlayer()
            _playerState.value = PlayerScreenState.Playing
            updatePlayedTime()
        }
    }

    private fun updatePlayedTime() {
        timerRunnable = object : Runnable  {
            override fun run() {
                if (_playerState.value == PlayerScreenState.Playing) {
                    _currentTime.value = playerInteractor.getCurrentPosition()
                    mainHandler.postDelayed(this, TIMER_STEP / 2)
                }
            }
        }
        mainHandler.post(timerRunnable!!)
    }

    private fun stopUpdaitingTime() {
        timerRunnable?.let { mainHandler.removeCallbacks(it) }
    }

    fun pause() {
        if (_playerState.value != PlayerScreenState.Paused) {
            playerInteractor.pausePlayer()
            _playerState.value = PlayerScreenState.Paused
            stopUpdaitingTime()
        }
    }



    fun setCurrentTrack(trackJsonString: String) {
        _trackState.postValue(SelectedTrackState.Loading)
        playerInteractor.getTrackDetails(trackJsonString, object : PlayerInteractor.TrackConsumer {
            override fun consume(track: Track) {
                mainHandler.post {
                    try {
                        _trackState.postValue(SelectedTrackState.Content(track))
                        preparePlayer(track)
                    } catch (e: Exception) {
                        Log.e(TAG, "Error in consume method", e)
                    }
                }
            }

            override fun onError(t: Throwable) {
                mainHandler.post {
                    Log.e(TAG, "Ошибка: ${t.message}", t)
                }
            }
        })
    }

    private fun preparePlayer(track: Track) {
        playerInteractor.preparePlayer(track, onPrepared = {
            _playerState.postValue(PlayerScreenState.Prepared)
        }, onCompletion = {
            _playerState.postValue(PlayerScreenState.Prepared)
        })
    }

    public override fun onCleared() {
        super.onCleared()
        stopUpdaitingTime()
        playerInteractor.onDestroy()
    }

    companion object {
        private const val TIMER_STEP = 1_000L

        fun getViewModelFactory(): ViewModelProvider.Factory =
            viewModelFactory {
                initializer {
                    PlayerViewModel(
                        Creator.providePlayerInteractor()
                    )
                }
            }

    }
}