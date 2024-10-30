package com.example.playlistmaker.mediateka.data

import android.util.Log
import com.example.playlistmaker.mediateka.domain.Playlist
import com.example.playlistmaker.mediateka.domain.PlaylistInteractor
import com.example.playlistmaker.mediateka.domain.PlaylistRepository
import com.example.playlistmaker.mediateka.domain.TrackInPlaylistRepository
import com.example.playlistmaker.search.domain.Track
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class PlaylistInteractorImpl(
    val playlistRepository: PlaylistRepository,
    val trackInPlaylistRepository: TrackInPlaylistRepository
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
        val trackIdsList =
            PlaylistDbMapper.toTracksList(playlistRepository.getPlaylistDetails(playlistName).tracks)
        trackIdsList.forEach { trackId ->
            trackInPlaylistRepository.removeTrackFromDatabase(trackId, playlistName)
        }

        playlistRepository.deletePlaylist(playlistName)

    }

    override suspend fun updateTracksInPlaylist(
        playlistName: String,
        track: Track,
        isAdding: Boolean
    ) {
        playlistRepository.updateTracksInPlaylist(playlistName, track, isAdding)
        if (isAdding) {
            trackInPlaylistRepository.addTrackInDatabase(track)
        } else {
            trackInPlaylistRepository.removeTrackFromDatabase(track.trackId, playlistName)
        }
    }

    override fun getAllPlaylists(): Flow<List<Playlist>> {
        return playlistRepository.getAllPlaylists()
    }

    override fun getAllPlaylistsNames(): Flow<List<String>> {
        return playlistRepository.getAllPlaylistsNames()
    }

    override suspend fun getPlaylistDetails(playlistName: String): Pair<Playlist, List<Track>> {
        val playlist = playlistRepository.getPlaylistDetails(playlistName)
        val trackList =
            trackInPlaylistRepository.fetchTracksByIds(PlaylistDbMapper.toTracksList(playlist.tracks))
                .first()
        return Pair(playlist, trackList)
    }

    override suspend fun editPlaylist(
        playlistName: String,
        newPlaylistName: String,
        description: String,
        cover: String?
    ) {
        playlistRepository.updatePlaylist(playlistName, newPlaylistName, description, cover)
    }


}