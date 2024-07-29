package com.example.playlistmaker.creator

import android.content.Context
import com.example.playlistmaker.config.data.ThemeRepositoryImpl
import com.example.playlistmaker.config.domain.ThemeRepository
import com.example.playlistmaker.player.data.MediaPlayerHandlerImpl
import com.example.playlistmaker.player.data.PlayerHandler
import com.example.playlistmaker.search.data.network.RetrofitNetworkClient
import com.example.playlistmaker.search.data.network.TracksRepositoryImpl
import com.example.playlistmaker.player.data.SelectedTrackRepositoryImpl
import com.example.playlistmaker.player.domain.PlayerInteractor
import com.example.playlistmaker.player.domain.SelectedTrackRepository
import com.example.playlistmaker.search.domain.TracksInteractor
import com.example.playlistmaker.search.domain.TracksRepository
import com.example.playlistmaker.player.domain.PlayerInteractorImpl
import com.example.playlistmaker.search.domain.TracksInteractorImpl

object Creator {
    private fun getTracksRepository(context: Context): TracksRepository {
        return TracksRepositoryImpl(RetrofitNetworkClient(context))
    }

    fun provideTracksInteractor(context: Context): TracksInteractor {
        return TracksInteractorImpl(getTracksRepository(context))
    }

    fun getSelectedTrackRepository(): SelectedTrackRepository {
        return SelectedTrackRepositoryImpl()
    }

    fun providePlayerInteractor(): PlayerInteractor {
        return PlayerInteractorImpl(getPlayerHandler(), getSelectedTrackRepository())
    }

    private fun getPlayerHandler(): PlayerHandler {
        return MediaPlayerHandlerImpl()
    }

    fun getThemeRepository(context: Context): ThemeRepository {
        return ThemeRepositoryImpl(context)
    }

}