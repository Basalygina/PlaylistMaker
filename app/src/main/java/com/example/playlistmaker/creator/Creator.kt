package com.example.playlistmaker.creator

import android.content.Context
import com.example.playlistmaker.settings.data.ThemeRepositoryImpl
import com.example.playlistmaker.settings.domain.ThemeInteractor
import com.example.playlistmaker.settings.domain.ThemeInteractorImpl
import com.example.playlistmaker.settings.domain.ThemeRepository
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
import com.example.playlistmaker.search.data.SearchHistoryRepositoryImpl
import com.example.playlistmaker.search.domain.SearchHistoryRepository
import com.example.playlistmaker.search.domain.TracksInteractorImpl

object Creator {
    private fun getTracksRepository(context: Context): TracksRepository {
        return TracksRepositoryImpl(RetrofitNetworkClient(context))
    }

    fun provideTracksInteractor(context: Context): TracksInteractor {
        return TracksInteractorImpl(
            getTracksRepository(context),
            getSearchHistoryRepository(context),
            getSelectedTrackRepository()
        )
    }

    private fun getSearchHistoryRepository(context: Context): SearchHistoryRepository{
        return SearchHistoryRepositoryImpl(context)
    }

    private fun getSelectedTrackRepository(): SelectedTrackRepository {
        return SelectedTrackRepositoryImpl()
    }

    fun providePlayerInteractor(): PlayerInteractor {
        return PlayerInteractorImpl(getPlayerHandler(), getSelectedTrackRepository())
    }

    private fun getPlayerHandler(): PlayerHandler {
        return MediaPlayerHandlerImpl()
    }

    private fun getThemeRepository(context: Context): ThemeRepository {
        return ThemeRepositoryImpl(context)
    }

    fun provideThemeInteractor(context: Context): ThemeInteractor {
        return ThemeInteractorImpl(getThemeRepository(context))
    }

}