package com.example.playlistmaker

import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.Track.Companion.TRACK_DATA
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerActivity : AppCompatActivity() {
    private val gson = Gson()
    private lateinit var buttonBack: ImageView
    private lateinit var playButton: ImageView
    private lateinit var queueButton: ImageView
    private lateinit var favoriteButton: ImageView

    private lateinit var albumCover: ImageView
    private lateinit var trackName: TextView
    private lateinit var artistName: TextView
    private lateinit var trackTime: TextView
    private lateinit var collectionName: TextView
    private lateinit var releaseDate: TextView
    private lateinit var primaryGenreName: TextView
    private lateinit var country: TextView

    private lateinit var trackTimeLabel: TextView
    private lateinit var collectionNameLabel: TextView
    private lateinit var releaseDateLabel: TextView
    private lateinit var primaryGenreNameLabel: TextView
    private lateinit var countryLabel: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)
        val track = intent.getSerializableExtra(TRACK_DATA) as Track
        val dateFormat by lazy { SimpleDateFormat("mm:ss", Locale.getDefault()) }
        val constraintLayout = findViewById<ConstraintLayout>(R.id.player_layout)
        val constraintSet = ConstraintSet()
        constraintSet.clone(constraintLayout)
        buttonBack = findViewById(R.id.button_back)
        playButton = findViewById(R.id.button_play)
        queueButton = findViewById(R.id.button_queue)
        favoriteButton = findViewById(R.id.button_favorite)

        albumCover = findViewById(R.id.album_cover)
        trackName = findViewById(R.id.track_name)
        artistName = findViewById(R.id.artist_name)
        trackTime = findViewById(R.id.track_time_data)
        collectionName = findViewById(R.id.collection_data)
        releaseDate = findViewById(R.id.year_data)
        primaryGenreName = findViewById(R.id.genre_data)
        country = findViewById(R.id.country_data)

        trackTimeLabel = findViewById(R.id.track_time_label)
        collectionNameLabel = findViewById(R.id.collection_label)
        releaseDateLabel = findViewById(R.id.year_label)
        primaryGenreNameLabel = findViewById(R.id.genre_label)
        countryLabel = findViewById(R.id.country_label)

        trackName.text = track.trackName
        artistName.text = track.artistName
        trackTime.text = dateFormat.format(track.trackTimeMillis)
        collectionName.text = track.collectionName
        releaseDate.text = track.releaseDate.substring(0, 4)
        primaryGenreName.text = track.primaryGenreName
        country.text = track.country

        Glide.with(applicationContext)
            .load(track.artworkUrl512)
            .placeholder(R.drawable.placeholder_large)
            .centerCrop()
            .transform(RoundedCorners(4))
            .into(albumCover)

        if (track.collectionName.isEmpty()) {
            collectionNameLabel.text = ""
            constraintSet.connect(R.id.year_label,ConstraintSet.TOP,R.id.h_guideLine_8,ConstraintSet.BOTTOM)
            constraintSet.connect(R.id.genre_label,ConstraintSet.TOP,R.id.h_guideLine_9,ConstraintSet.BOTTOM)
            constraintSet.connect(R.id.country_label, ConstraintSet.TOP, R.id.h_guideLine_10, ConstraintSet.BOTTOM)
            constraintSet.applyTo(constraintLayout)
        }

        buttonBack.setOnClickListener {
            onBackPressed()
        }


    }


}