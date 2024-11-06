package com.example.playlistmaker.mediateka.ui.playlists

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.mediateka.data.FileManager
import com.example.playlistmaker.mediateka.domain.Playlist
import com.example.playlistmaker.mediateka.domain.PlaylistInteractor

open class CreatePlaylistViewModel(
    private val fileManager: FileManager,
    private val playlistInteractor: PlaylistInteractor
) : ViewModel() {

    fun saveCover(uri: Uri) =
        fileManager.saveImageToAppStorage(uri)


    suspend fun createPlaylist(name: String, description: String?, coverImagePath: String?) =
        playlistInteractor.createPlaylist(name, description, coverImagePath)

}