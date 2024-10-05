package com.example.playlistmaker.mediateka.data

import com.example.playlistmaker.mediateka.data.db.TrackEntity
import com.example.playlistmaker.search.domain.Track

object TrackDbMapper {

    fun Track.toTrackEntity(): TrackEntity {
        return TrackEntity(
            this.trackId,
            this.trackName,
            this.artistName,
            this.trackTimeString,
            this.artworkUrl100,
            this.collectionName,
            this.releaseDate,
            this.primaryGenreName,
            this.country,
            this.previewUrl
        )
    }

    fun TrackEntity.toTrack(): Track {
        return Track(
            this.trackId,
            this.trackName,
            this.artistName,
            this.trackTimeString,
            this.artworkUrl100,
            this.collectionName,
            this.releaseDate,
            this.primaryGenreName,
            this.country,
            this.previewUrl
        )
    }

}