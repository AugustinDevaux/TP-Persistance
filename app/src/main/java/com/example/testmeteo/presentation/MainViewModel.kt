package com.example.testmeteo.presentation

import androidx.lifecycle.ViewModel

class MainViewModel : IMainContract.ViewModel, ViewModel() {
    override fun fetchWeatherData(cities: List<String>) {
        TODO("Not yet implemented")
    }

    override fun splitCitiesNames(userInput: String): List<String> {
       return userInput.split(",")
    }
}