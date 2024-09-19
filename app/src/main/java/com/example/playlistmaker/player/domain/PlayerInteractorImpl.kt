package com.example.playlistmaker.player.domain

import com.example.playlistmaker.player.data.PlayerHandler
import com.example.playlistmaker.search.domain.Track

class PlayerInteractorImpl(
    private val playerHandler: PlayerHandler,
    private val repository: SelectedTrackRepository
) : PlayerInteractor {

    override suspend fun getTrackDetails(
        trackJsonString: String
    ) = repository.decodeTrackDetails(trackJsonString)

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
        playerHandler.stopPlayer()
    }

}