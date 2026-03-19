package com.example.theflower

import android.app.Application
import com.example.theflower.BuildConfig
import timber.log.Timber

class TheFlowerApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
            Timber.tag("AppLogger").d("Timber initialized in DEBUG mode")
        }
    }
}
