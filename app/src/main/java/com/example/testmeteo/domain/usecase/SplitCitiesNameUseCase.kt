package com.example.testmeteo.domain.usecase

class SplitCitiesNameUseCase {


    fun execute(userInput: String): List<String>{
        return userInput.split(",")
    }
}