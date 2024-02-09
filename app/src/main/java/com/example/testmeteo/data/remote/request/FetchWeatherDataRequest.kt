package com.example.testmeteo.data.remote.request

import com.example.testmeteo.data.remote.Weather
import com.example.testmeteo.network.WeatherApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class FetchWeatherDataRequest {

    private val apiKey = "bd5e378503939ddaee76f12ad7a97608"

    private val apiService: WeatherApiService by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(WeatherApiService::class.java)
    }

    suspend fun fetchWeatherData(cities: List<String>): List<Weather?> {
        return coroutineScope {
            // Utiliser async pour lancer plusieurs coroutines en parallèle
            val deferredWeather = cities.map { city ->
                async(Dispatchers.IO) {
                    // Faire un appel à l'API pour chaque ville
                    val response = apiService.getWeather(city, apiKey).execute()
                    if (response.isSuccessful) {
                        // Si l'appel est réussi, renvoyer le résultat
                        response.body()
                    } else {
                        // Sinon, renvoyer null
                        null
                    }
                }
            }
            // Utiliser awaitAll pour attendre que toutes les coroutines se terminent
            val weathers = deferredWeather.awaitAll()
            // Traiter les résultats
            val nonEmptyWeathers: MutableList<Weather> = arrayListOf()
            weathers.mapNotNull {it}
        }
    }
}