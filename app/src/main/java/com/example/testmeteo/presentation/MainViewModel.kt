package com.example.testmeteo.presentation

import androidx.lifecycle.ViewModel
import com.example.testmeteo.domain.usecase.SplitCitiesNameUseCase

class MainViewModel : IMainContract.ViewModel, ViewModel() {

    private val splitCitiesNameUseCase = SplitCitiesNameUseCase()

    override fun fetchWeatherData(cities: List<String>) {
        TODO("Not yet implemented")
    }

    override fun splitCitiesNames(userInput: String): List<String> {
       return splitCitiesNameUseCase.execute(userInput)
    }
}