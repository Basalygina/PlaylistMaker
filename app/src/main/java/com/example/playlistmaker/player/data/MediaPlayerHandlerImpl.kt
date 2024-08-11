package com.example.playlistmaker.player.data

import android.media.MediaPlayer
import com.example.playlistmaker.search.domain.Track
import java.text.SimpleDateFormat
import java.util.Locale

class MediaPlayerHandlerImpl(private var mediaPlayer: MediaPlayer) : PlayerHandler {

    override fun preparePlayer(track: Track, onPrepared: () -> Unit, onCompletion: () -> Unit) {
        mediaPlayer.reset()  // Сбрасываем MediaPlayer перед его повторным использованием
        mediaPlayer.setDataSource(track.previewUrl)
        mediaPlayer.setOnPreparedListener {
            onPrepared()
        }
        mediaPlayer.setOnCompletionListener {
            onCompletion()
        }
        mediaPlayer.prepareAsync()
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

    override fun stopPlayer() {
        mediaPlayer.stop()
    }


}