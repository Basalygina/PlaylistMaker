package com.example.playlistmaker.mediateka.domain

import java.io.Serializable

data class Playlist(
    val playlistName: String, // Название плейлиста
    val description: String?, // Описание
    val cover: String?, // Путь к файлу обложки
    val tracks: String = "", // Список идентификаторов треков
    val tracksCount: Int = 0, // Количество треков
    val durationSum: Long = 0L //Общая длительность треков в миллисекундах
) : Serializable {

    fun containsTrack(trackId: Int): Boolean {
        return tracks.split(",").map { it.trim() }.contains(trackId.toString())
    }

    companion object{
        const val ARG_PLAYLIST_NAME = "playlist_name"
    }

}