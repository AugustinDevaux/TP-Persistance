package com.example.testmeteo.domain.usecase.repository

import com.example.testmeteo.data.remote.Weather

interface IWeatherRepository {

    suspend fun fetchWeatherData(cities: List<String>): List<Weather?>
}