package com.example.playlistmaker.mediateka.data

import com.example.playlistmaker.mediateka.data.PlaylistDbMapper.toPlaylist
import com.example.playlistmaker.mediateka.data.PlaylistDbMapper.toPlaylistEntity
import com.example.playlistmaker.mediateka.data.db.PlaylistDatabase
import com.example.playlistmaker.mediateka.domain.Playlist
import com.example.playlistmaker.mediateka.domain.PlaylistRepository
import com.example.playlistmaker.search.data.TrackMapper
import com.example.playlistmaker.search.domain.Track
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class PlaylistRepositoryImpl(val playlistDatabase: PlaylistDatabase) : PlaylistRepository {
    override suspend fun createPlaylist(playlist: Playlist) = withContext(Dispatchers.IO) {
        playlistDatabase.daoPlaylist().createPlaylist(playlist.toPlaylistEntity())
    }

    override suspend fun deletePlaylist(playlistName: String) = withContext(Dispatchers.IO) {
        playlistDatabase.daoPlaylist().deletePlaylist(playlistName)
    }

    override suspend fun updateTracksInPlaylist(
        playlistName: String,
        track: Track,
        isAdding: Boolean
    ) = withContext(Dispatchers.IO) {
        val playlist = playlistDatabase.daoPlaylist().getPlaylistDetails(playlistName)
        val tracksList = PlaylistDbMapper.toTracksList(playlist.tracks).toMutableList()
        if (isAdding) tracksList.add(track.trackId) else tracksList.remove(track.trackId)

        val updatedTracks = PlaylistDbMapper.fromTracksList(tracksList.toList())
        val updatedDurationSum = playlist.durationSum +
                TrackMapper.convertStringToMillis(track.trackTimeString) * if (isAdding) 1 else -1
        playlistDatabase.daoPlaylist()
            .updateTracksInPlaylist(
                playlist.playlistName,
                updatedTracks,
                tracksList.size,
                updatedDurationSum
            )
    }

    override fun getAllPlaylists(): Flow<List<Playlist>> {
        return playlistDatabase.daoPlaylist().getAllPlaylists()
            .map { playlistEntities ->
                playlistEntities.map { it.toPlaylist() }
            }
    }

    override fun getAllPlaylistsNames(): Flow<List<String>> {
        return playlistDatabase.daoPlaylist().getAllPlaylistsNames()
    }

    override suspend fun getPlaylistDetails(playlistName: String): Playlist =
        withContext(Dispatchers.IO) {
            return@withContext playlistDatabase.daoPlaylist().getPlaylistDetails(playlistName)
                .toPlaylist()
        }

    override suspend fun updatePlaylist(
        playlistName: String,
        newPlaylistName: String,
        description: String,
        cover: String?
    ) = withContext(Dispatchers.IO)  {
        if (cover != null) {
            playlistDatabase.daoPlaylist()
                .updatePlaylist(playlistName, newPlaylistName, description, cover)
        } else {
            playlistDatabase.daoPlaylist()
                .updatePlaylist(playlistName, newPlaylistName, description, "")
        }
    }

}