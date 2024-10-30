package com.example.playlistmaker.mediateka.ui

import android.util.Log

object MediaTextFormatter {
    fun getTrackDeclension(tracksCount: Int): String {
        return when {
            tracksCount % 10 == 1 && tracksCount % 100 != 11 -> "трек"
            tracksCount % 10 in 2..4 && tracksCount % 100 !in 12..14 -> "трека"
            else -> "треков"
        }
    }

    fun getMinutesDeclension(minutes: Int): String {
        return when {
            minutes % 10 == 1 && minutes % 100 != 11 -> "минута"
            minutes % 10 in 2..4 && minutes % 100 !in 12..14 -> "минуты"
            else -> "минут"
        }
    }
}