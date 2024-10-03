package com.example.playlistmaker.di

import com.example.playlistmaker.mediateka.data.FavTracksInteractorImpl
import com.example.playlistmaker.mediateka.domain.FavTracksInteractor
import com.example.playlistmaker.player.domain.PlayerInteractor
import com.example.playlistmaker.player.domain.PlayerInteractorImpl
import com.example.playlistmaker.search.domain.TracksInteractor
import com.example.playlistmaker.search.domain.TracksInteractorImpl
import com.example.playlistmaker.settings.domain.ThemeInteractor
import com.example.playlistmaker.settings.domain.ThemeInteractorImpl
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val interactorModule = module {

    factoryOf(::ThemeInteractorImpl) { bind<ThemeInteractor>() }
    factoryOf(::TracksInteractorImpl) { bind<TracksInteractor>() }
    factoryOf(::PlayerInteractorImpl) { bind<PlayerInteractor>() }
    factoryOf(::FavTracksInteractorImpl) { bind<FavTracksInteractor>() }

}