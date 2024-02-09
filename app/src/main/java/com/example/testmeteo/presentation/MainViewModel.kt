package com.example.testmeteo.presentation

import androidx.lifecycle.ViewModel
import com.example.testmeteo.data.remote.Weather
import com.example.testmeteo.domain.usecase.usecase.FetchWeatherDataUseCase
import com.example.testmeteo.domain.usecase.usecase.SplitCitiesNameUseCase

class MainViewModel : IMainContract.ViewModel, ViewModel() {

    private val splitCitiesNameUseCase = SplitCitiesNameUseCase()
    private val fetchWeatherDataUseCase = FetchWeatherDataUseCase()

    override suspend fun fetchWeatherData(cities: List<String>) : List<Weather?>{
        return fetchWeatherDataUseCase.execute(cities)
    }

    override fun splitCitiesNames(userInput: String): List<String> {
       return splitCitiesNameUseCase.execute(userInput)
    }
}