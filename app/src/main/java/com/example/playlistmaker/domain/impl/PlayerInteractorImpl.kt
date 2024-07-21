package com.example.playlistmaker.domain.impl

import com.example.playlistmaker.data.handler.PlayerHandler
import com.example.playlistmaker.domain.api.interactors.PlayerInteractor
import com.example.playlistmaker.domain.api.repository.SelectedTrackRepository
import com.example.playlistmaker.domain.models.Track
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.Executors

class PlayerInteractorImpl(
    private val playerHandler: PlayerHandler,
    private val repository: SelectedTrackRepository
) : PlayerInteractor {
    private val executor = Executors.newCachedThreadPool()


    override fun getTrackDetails(
        trackJsonString: String,
        consumer: PlayerInteractor.TrackConsumer
    ) {
        executor.execute {
            try {
                val track = repository.decodeTrackDetails(trackJsonString)
                consumer.consume(track)
            } catch (t: Throwable) {
                consumer.onError(t)
            }
        }
    }

    override fun preparePlayer(track: Track, onPrepared: () -> Unit, onCompletion: () -> Unit) {
        playerHandler.preparePlayer(track, onPrepared, onCompletion)
    }


    override fun startPlayer() {
        playerHandler.play()
    }

    override fun pausePlayer() {
        playerHandler.pause()
    }

    override fun getCurrentPosition(): String {
        return playerHandler.getCurrentPosition()
    }

    override fun onDestroy() {
        playerHandler.shutdownPlayer()
    }

}