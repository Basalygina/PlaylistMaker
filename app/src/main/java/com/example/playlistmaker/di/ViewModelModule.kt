package com.example.playlistmaker.di

import com.example.playlistmaker.mediateka.ui.FavoriteViewModel
import com.example.playlistmaker.mediateka.ui.PlaylistsViewModel
import com.example.playlistmaker.player.ui.PlayerViewModel
import com.example.playlistmaker.search.domain.Track
import com.example.playlistmaker.search.ui.SearchViewModel
import com.example.playlistmaker.settings.ui.SettingsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {

    viewModel {
        SettingsViewModel(get())
    }

    viewModel {
        SearchViewModel(get(), get())
    }

    viewModel {
        PlayerViewModel(get(), get())
    }

    viewModel {(favoriteTracks: List<Track>) ->
        FavoriteViewModel(favoriteTracks)
    }

    viewModel {
        PlaylistsViewModel()
    }

}