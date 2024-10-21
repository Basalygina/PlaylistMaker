package com.example.playlistmaker.di

import android.content.Context
import android.content.SharedPreferences
import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import androidx.room.Room
import com.example.playlistmaker.config.App
import com.example.playlistmaker.mediateka.data.FileManager
import com.example.playlistmaker.mediateka.data.db.PlaylistDatabase
import com.example.playlistmaker.player.data.MediaPlayerHandlerImpl
import com.example.playlistmaker.player.data.PlayerHandler
import com.example.playlistmaker.search.data.NetworkClient
import com.example.playlistmaker.search.data.network.RetrofitNetworkClient
import com.example.playlistmaker.search.data.network.iTunesSearchApi
import com.google.gson.Gson
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.Executor
import java.util.concurrent.Executors


val dataModule = module {

    single {
        Room.databaseBuilder(androidContext(), PlaylistDatabase::class.java, "playlists_database.db")
            .fallbackToDestructiveMigration()
            .build()
    }

    single { get<PlaylistDatabase>().favDao() }
    single { get<PlaylistDatabase>().playlistDao() }

    single<NetworkClient> {
        RetrofitNetworkClient(get(), androidContext())
    }

    single<iTunesSearchApi> {
        Retrofit.Builder()
            .baseUrl("https://itunes.apple.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(iTunesSearchApi::class.java)
    }


    single<PlayerHandler> {
        MediaPlayerHandlerImpl(get())
    }

    single<MediaPlayer> {
        MediaPlayer()
    }

    single<Executor> {
        Executors.newCachedThreadPool()
    }

    single<Handler>{
        Handler(Looper.getMainLooper())
    }

    factory<Gson> { Gson() }

    single<SharedPreferences> {
        androidContext().getSharedPreferences(App.PM_PREFERENCES, Context.MODE_PRIVATE)
    }

    single { FileManager(androidContext()) }

}
