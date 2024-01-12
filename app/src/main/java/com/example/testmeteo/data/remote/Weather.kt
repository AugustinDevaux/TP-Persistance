package com.example.testmeteo.data.remote

data class Weather (
    val main: Main,
    val weather: List<WeatherInfo>,
    val name: String,
    val dt: Long
)
