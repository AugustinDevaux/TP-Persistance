package com.example.testmeteo.network;

import com.example.testmeteo.data.remote.Weather
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

public interface WeatherApiService {
    @GET("weather")
    fun getWeather(
        @Query("q") city: String, @Query("appid") apiKey: String
    ): Call<Weather>
}
