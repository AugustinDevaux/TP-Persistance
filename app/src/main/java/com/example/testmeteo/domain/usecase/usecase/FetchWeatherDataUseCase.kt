package com.example.testmeteo.domain.usecase.usecase

import com.example.testmeteo.data.remote.Weather
import com.example.testmeteo.data.repository.WeatherRepository

class FetchWeatherDataUseCase {

    private val repository = WeatherRepository()

    suspend fun execute(cities: List<String>): List<Weather?> {
        return repository.fetchWeatherData(cities)
    }

}