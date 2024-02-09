package com.example.testmeteo.presentation


internal interface IMainContract {

    interface ViewModel {

        fun fetchWeatherData(cities: List<String>)
        fun splitCitiesNames(userInput: String): List<String>
    }
}
