package com.example.testmeteo.domain.usecase.usecase

class SplitCitiesNameUseCase {


    fun execute(userInput: String): List<String>{
        return userInput.split(",")
    }
}