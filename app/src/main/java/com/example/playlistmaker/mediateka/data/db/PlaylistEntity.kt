package com.example.playlistmaker.mediateka.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "playlists")
data class PlaylistEntity(
    val playlistName: String, // Название плейлиста
    val description: String?, // Описание
    val cover: String?, // Путь к файлу обложки
    val tracks: String = "", // Список идентификаторов треков
    val tracksCount: Int = 0, // Количество треков
    val durationSum: Long = 0L, // Общая длительность треков в миллисекундах
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null

)