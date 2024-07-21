package com.example.playlistmaker.data.handler

import android.media.MediaPlayer
import com.example.playlistmaker.domain.api.interactors.PlayerInteractor
import com.example.playlistmaker.domain.models.Track
import java.text.SimpleDateFormat
import java.util.Locale

class MediaPlayerHandlerImpl: PlayerHandler {
    private val mediaPlayer = MediaPlayer()

    override fun preparePlayer(track: Track, onPrepared: () -> Unit, onCompletion: () -> Unit) {
        mediaPlayer.setDataSource(track.previewUrl)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            onPrepared()
        }
        mediaPlayer.setOnCompletionListener {
            onCompletion()
        }
    }

    override fun play() {
        mediaPlayer.start()
    }

    override fun pause() {
        mediaPlayer.pause()
    }

    override fun getCurrentPosition(): String {
        return SimpleDateFormat("mm:ss", Locale.getDefault()).format(mediaPlayer.currentPosition)
    }

    override fun shutdownPlayer() {
        mediaPlayer.release()
    }


}