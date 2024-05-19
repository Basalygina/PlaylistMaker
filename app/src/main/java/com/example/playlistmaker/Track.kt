package com.example.playlistmaker

data class Track(
    val trackId: Int,
    val trackName: String, // Название композиции
    val artistName: String, // Имя исполнителя
    val trackTimeMillis: Int, // Продолжительность трека
    val artworkUrl100: String // Ссылка на изображение обложки
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        if (trackId != (other as Track).trackId) return false

        return true
    }

    override fun hashCode(): Int {
        return trackId.hashCode()
    }
}