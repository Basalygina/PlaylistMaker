package com.example.playlistmaker.data.network

import com.example.playlistmaker.data.NetworkClient
import com.example.playlistmaker.data.TrackMapper.toDomainModel
import com.example.playlistmaker.data.dto.SearchRequest
import com.example.playlistmaker.data.dto.SearchResponse
import com.example.playlistmaker.domain.api.repository.TracksRepository
import com.example.playlistmaker.domain.models.Track

class TracksRepositoryImpl(private val networkClient: NetworkClient) : TracksRepository {
    override fun searchTracks(expression: String): List<Track> {
        val response = networkClient.doRequest(SearchRequest(expression))
        if (response.resultCode == 200) {

            return (response as SearchResponse).results.map {
                it.toDomainModel()
            }
        } else {
            return emptyList()
        }
    }

}
