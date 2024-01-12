package com.example.testmeteo.data.remote

class Weather {
    val main: Main,
    val weather: List<WeatherInfo>,
    val name: String,
    val dt: Long
}

class Main {
    val temp: Double,
    val humidity: Int
}

class WeatherInfo {
    val description: String,
    val icon: String
}