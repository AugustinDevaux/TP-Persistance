package com.example.weather.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "weather_table")
data class WeatherEntity(
    @PrimaryKey (autoGenerate = true)
    val id: Long = 0,
    val cityName: String,
    val temperature: Double,
    val humidity: Int,
    val description: String,
    val icon: String,
    val timestamp: Long = System.currentTimeMillis()
)