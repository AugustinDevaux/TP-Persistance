package com.example.testmeteo.network;

import androidx.contentpager.content.Query;
import com.google.android.gms.awareness.state.Weather

public interface WeatherApiService {
    @GET("weather")
    fun getWeather(
        @Query("q") city: String, @Query("appid") apiKey: String
    ): Call<Weather>
}
