package com.example.playlistmaker.mediateka.data

import android.util.Log
import com.example.playlistmaker.mediateka.domain.Playlist
import com.example.playlistmaker.mediateka.domain.PlaylistInteractor
import com.example.playlistmaker.mediateka.domain.PlaylistRepository
import com.example.playlistmaker.search.domain.Track
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PlaylistInteractorImpl(
    val playlistRepository: PlaylistRepository
) : PlaylistInteractor {

    override suspend fun createPlaylist(
        name: String,
        description: String?,
        coverImagePath: String?
    ): Boolean {
        return coroutineScope {
            val existingNames = playlistRepository.getAllPlaylistsNames().first()

            val needToCreateNewPlaylist = existingNames.contains(name).not()

            val createPlaylistJob = async {
                if (needToCreateNewPlaylist) {
                    playlistRepository.createPlaylist(Playlist(name, description, coverImagePath))
                    true
                } else {
                    false
                }
            }

            createPlaylistJob.await()
        }
    }

    override suspend fun deletePlaylist(playlistName: String) {
        playlistRepository.deletePlaylist(playlistName)
    }

    override suspend fun updatePlaylist(playlist: Playlist, track: Track) {
        playlistRepository.updatePlaylist(playlist, track)
    }

    override fun getAllPlaylists(): Flow<List<Playlist>> {
        return playlistRepository.getAllPlaylists()
    }

    override fun getAllPlaylistsNames(): Flow<List<String>> {
        return playlistRepository.getAllPlaylistsNames()
    }
}