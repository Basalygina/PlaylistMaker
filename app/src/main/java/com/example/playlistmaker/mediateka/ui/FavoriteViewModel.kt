package com.example.playlistmaker.mediateka.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.search.domain.Track

class FavoriteViewModel(private val favoriteTracks: List<Track>): ViewModel()  {
    private val favoriteLiveData = MutableLiveData<List<Track>>(favoriteTracks)

    fun observeFavorite(): LiveData<List<Track>> = favoriteLiveData
}