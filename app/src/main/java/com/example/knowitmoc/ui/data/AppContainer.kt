package com.example.knowitmoc.ui.data

import com.example.knowitmoc.ui.screens.FlickrApiService
import com.example.knowitmoc.ui.screens.FlickrPhotosRepository
import com.example.knowitmoc.ui.screens.NetworkFlickrPhotosRepository
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

interface AppContainer {
    val flickrPhotosRepository : FlickrPhotosRepository
}

class DefaultAppContainer : AppContainer {

    private val BASE_URL = "https://api.flickr.com/services/rest/"

    private val retrofit: Retrofit = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(BASE_URL)
        .build()

    private val retrofitService: FlickrApiService by lazy {
        retrofit.create(FlickrApiService::class.java)
    }

    override val flickrPhotosRepository: FlickrPhotosRepository by lazy {
        NetworkFlickrPhotosRepository(retrofitService)
    }
}