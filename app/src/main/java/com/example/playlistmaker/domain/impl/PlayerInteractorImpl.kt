package com.example.playlistmaker.domain.impl

import android.media.MediaPlayer
import com.example.playlistmaker.domain.api.interactors.PlayerInteractor
import com.example.playlistmaker.domain.api.repository.SelectedTrackRepository
import com.example.playlistmaker.domain.models.Track
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.Executors

class PlayerInteractorImpl(private val repository: SelectedTrackRepository): PlayerInteractor {
    private val executor = Executors.newCachedThreadPool()
    private val mediaPlayer = MediaPlayer()

    override fun getTrackDetails(trackJsonString: String, consumer: PlayerInteractor.TrackConsumer) {
        executor.execute {
            try {
                val track = repository.getTrackDetails(trackJsonString)
                consumer.consume(track)
            } catch (t: Throwable) {
                consumer.onError(t)
            }
        }
    }

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



    override fun startPlayer() {
        mediaPlayer.start()
    }

    override fun pausePlayer() {
        mediaPlayer.pause()
    }

    override fun getCurrentPosition(): String {
        return SimpleDateFormat("mm:ss", Locale.getDefault()).format(mediaPlayer.currentPosition)
    }

    override fun onDestroy() {
        mediaPlayer.release()
    }

}