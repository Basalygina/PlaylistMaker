package com.example.playlistmaker.di

import com.example.playlistmaker.mediateka.ui.favorite.FavoriteViewModel
import com.example.playlistmaker.mediateka.ui.playlists.CreatePlaylistViewModel
import com.example.playlistmaker.mediateka.ui.playlists.PlaylistsViewModel
import com.example.playlistmaker.player.ui.PlayerViewModel
import com.example.playlistmaker.search.ui.SearchViewModel
import com.example.playlistmaker.settings.ui.SettingsViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val viewModelModule = module {

    viewModelOf(::SettingsViewModel)
    viewModelOf(::SearchViewModel)
    viewModelOf(::PlayerViewModel)
    viewModelOf(::PlaylistsViewModel)
    viewModelOf(::FavoriteViewModel)
    viewModelOf(::CreatePlaylistViewModel)
}