package com.example.testmeteo

import android.app.Application
import com.example.testmeteo.data.local.WeatherDatabase

class WeatherApplication : Application() {
    companion object {
        lateinit var instance: WeatherApplication
            private set
        lateinit var weatherDatabase: WeatherDatabase
            private set
    }
    override fun onCreate() {
        super.onCreate()
        instance = this
        // Initialisation de la base de données Room
        weatherDatabase = WeatherDatabase.getDatabase(applicationContext)
    }
}