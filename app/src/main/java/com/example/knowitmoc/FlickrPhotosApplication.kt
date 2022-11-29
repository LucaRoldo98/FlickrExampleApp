package com.example.knowitmoc

import android.app.Application
import com.example.knowitmoc.ui.data.AppContainer
import com.example.knowitmoc.ui.data.DefaultAppContainer

class FlickrPhotosApplication : Application() {
    lateinit var container : AppContainer
    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer()
    }
}