package com.example.playlistmaker.search.data.network

import com.example.playlistmaker.search.data.NetworkClient
import com.example.playlistmaker.search.data.TrackMapper.toDomainModel
import com.example.playlistmaker.search.data.dto.SearchRequest
import com.example.playlistmaker.search.data.dto.SearchResponse
import com.example.playlistmaker.search.domain.Track
import com.example.playlistmaker.search.domain.TracksRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class TracksRepositoryImpl(private val networkClient: NetworkClient) : TracksRepository {
    override fun searchTracks(expression: String): Flow<MutableList<Track>> = flow {
        val response = networkClient.doRequest(SearchRequest(expression))
        if (response.resultCode == 200) {
            val tracks = (response as SearchResponse).results.map {
                it.toDomainModel()
            }.toMutableList()
            emit(tracks)
        } else {
            emit(emptyList<Track>().toMutableList())
        }
    }

}
