package com.example.playlistmaker.domain.models

import java.io.Serializable

data class Track(
    val trackId: Int,
    val trackName: String, // Название композиции
    val artistName: String, // Имя исполнителя
    val trackTimeString: String, // Продолжительность трека mm:ss
    val artworkUrl100: String, // Ссылка на изображение обложки
    val collectionName: String, // Альбом
    val releaseDate: String, // Год релиза
    val primaryGenreName: String, // Жанр
    val country: String, // Страна исполнителя
    val previewUrl: String // Отрывок трека
) : Serializable{
    val artworkUrl512
        get() = artworkUrl100.replaceAfterLast('/', "512x512bb.jpg")

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        if (trackId != (other as Track).trackId) return false

        return true
    }

    override fun hashCode(): Int {
        return trackId.hashCode()
    }

    companion object {
        const val TRACK_DATA = "TRACK_DATA"
        const val TRACK_DATA_ID = "TRACK_DATA_ID"
    }
}