package com.example.testmeteo.presentation

import com.example.testmeteo.data.remote.Weather


internal interface IMainContract {

    interface ViewModel {

        suspend fun fetchWeatherData(cities: List<String>): List<Weather?>
        fun splitCitiesNames(userInput: String): List<String>
    }
}
