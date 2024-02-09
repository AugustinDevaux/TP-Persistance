package com.example.testmeteo.data.repository

import com.example.testmeteo.data.remote.Weather
import com.example.testmeteo.data.remote.request.FetchWeatherDataRequest
import com.example.testmeteo.domain.usecase.repository.IWeatherRepository

class WeatherRepository : IWeatherRepository{

    private val fetchWeatherDataRequest = FetchWeatherDataRequest()

    override suspend fun fetchWeatherData(cities: List<String>): List<Weather?> {
        return fetchWeatherDataRequest.fetchWeatherData(cities)
    }
}