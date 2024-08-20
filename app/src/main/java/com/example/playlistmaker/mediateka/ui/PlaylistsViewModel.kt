package com.example.playlistmaker.mediateka.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.search.domain.Track

class PlaylistsViewModel: ViewModel()  {
    private val playlists = MutableLiveData<List<List<Track>>>()

    fun observePlaylists(): LiveData<List<List<Track>>> = playlists
}