package com.example.cleanarchitechture

import android.app.Application
import com.example.cleanarchitechture.di.appModule
import com.example.cleanarchitechture.di.viewModelModel
import org.koin.android.ext.android.startKoin

class App: Application() {

    override fun onCreate() {
        super.onCreate()
        instance = this
        startKoin(this, listOf(appModule, viewModelModel))
    }

    companion object {
        lateinit var instance: App
            private set
    }
}