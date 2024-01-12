package com.example.testmeteo.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.weather.data.local.WeatherEntity

@Dao
interface WeatherDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert (weatherEntity: WeatherEntity)

    @Query("SELECT * FROM weather_table WHERE cityName = :cityName ORDER BY timestamp DESC LIMIT 1")
    suspend fun getLatestWeather (cityName: String): WeatherEntity?
}