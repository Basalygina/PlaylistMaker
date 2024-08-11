package com.example.playlistmaker.di

import com.example.playlistmaker.player.domain.PlayerInteractor
import com.example.playlistmaker.player.domain.PlayerInteractorImpl
import com.example.playlistmaker.search.domain.TracksInteractor
import com.example.playlistmaker.search.domain.TracksInteractorImpl
import com.example.playlistmaker.settings.domain.ThemeInteractor
import com.example.playlistmaker.settings.domain.ThemeInteractorImpl
import org.koin.dsl.module

val interactorModule = module {

    single<ThemeInteractor> {
        ThemeInteractorImpl(get())
    }

    single<TracksInteractor> {
        TracksInteractorImpl(get(), get(), get(), get())
    }

    factory<PlayerInteractor> {
        PlayerInteractorImpl(get(), get(), get())
    }


}