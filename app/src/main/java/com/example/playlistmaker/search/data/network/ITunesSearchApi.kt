package com.example.playlistmaker.search.data.network

import com.example.playlistmaker.search.data.dto.SearchResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

fun interface iTunesSearchApi {
    @GET("/search?entity=song ")
    fun search(@Query("term") text: String): Call<SearchResponse>
}