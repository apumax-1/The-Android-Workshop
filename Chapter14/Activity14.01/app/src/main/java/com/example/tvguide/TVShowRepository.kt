package com.example.tvguide

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.tvguide.api.TelevisionService
import com.example.tvguide.database.TVDao
import com.example.tvguide.database.TVDatabase
import com.example.tvguide.model.TVShow
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class TVShowRepository(private val tvDatabase: TVDatabase) {
    private val apiKey = "your_api_key_here"

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.themoviedb.org/3/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private val tvService by lazy { retrofit.create(TelevisionService::class.java) }

    private var tvShows: MutableLiveData<List<TVShow>> = MutableLiveData()

    fun getTVShows(): LiveData<List<TVShow>> = tvShows

    suspend fun fetchTVShows() {
        val tvDao: TVDao = tvDatabase.tvDao()
        var shows = tvDao.getTVShows()
        if (shows.isEmpty()) {
            try {
                val tvResponse = tvService.getTVShows(apiKey)
                shows = tvResponse.results
                tvDao.addTVShows(shows)
            } catch (exception: Exception) {
                Log.d("TVShowRepository", "Exception in fetchTVShows: ${exception.message}")
            }
        }

        tvShows.postValue(shows)
    }

    suspend fun fetchTVShowsFromNetwork() {
        val tvDao: TVDao = tvDatabase.tvDao()
        var shows = tvDao.getTVShows()
        if (shows.isEmpty()) {
            try {
                val tvResponse = tvService.getTVShows(apiKey)
                shows = tvResponse.results
                tvDao.addTVShows(shows)
            } catch (exception: Exception) {
                Log.d("TVShowRepository", "Exception in fetchTVShowsFromNetwork: ${exception.message}")
            }
        }

        tvShows.postValue(shows)
    }
}