package com.rockbass.misclimas.app

import android.app.Application
import com.jakewharton.threetenabp.AndroidThreeTen
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.search.MapboxSearchSdk
import com.mapbox.search.location.DefaultLocationProvider
import com.rockbass.misclimas.BuildConfig
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class App : Application() {
    override fun onCreate() {
        super.onCreate()

        Mapbox.getInstance(this, BuildConfig.MapboxAcessToken)
        MapboxSearchSdk.initialize(
            this,
            BuildConfig.MapboxAcessToken,
            DefaultLocationProvider(this)
        )
        AndroidThreeTen.init(this)
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}