package com.example.regresoacasa

import android.app.Application
import com.example.regresoacasa.data.AppContainer
import com.example.regresoacasa.data.DefaultAppContainer

class AppAplication : Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer()
    }

    fun getAppContainer(): AppContainer {
        return container
    }
}