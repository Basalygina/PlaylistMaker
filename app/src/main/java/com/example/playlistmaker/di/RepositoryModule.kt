package com.example.playlistmaker.di

import android.content.Context
import com.example.playlistmaker.config.App
import com.example.playlistmaker.player.data.SelectedTrackRepositoryImpl
import com.example.playlistmaker.player.domain.SelectedTrackRepository
import com.example.playlistmaker.search.data.SearchHistoryRepositoryImpl
import com.example.playlistmaker.search.data.network.TracksRepositoryImpl
import com.example.playlistmaker.search.domain.SearchHistoryRepository
import com.example.playlistmaker.search.domain.TracksRepository
import com.example.playlistmaker.settings.data.ThemeRepositoryImpl
import com.example.playlistmaker.settings.domain.ThemeRepository
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val repositoryModule = module {

    single<ThemeRepository> {
        ThemeRepositoryImpl(
            androidContext()
                .getSharedPreferences(App.PM_PREFERENCES, Context.MODE_PRIVATE)
        )
    }

    single<TracksRepository> {
        TracksRepositoryImpl(get())
    }

    single<SearchHistoryRepository> {
        SearchHistoryRepositoryImpl(get(), get())
    }

    single<SelectedTrackRepository> {
        SelectedTrackRepositoryImpl(get())
    }
}
