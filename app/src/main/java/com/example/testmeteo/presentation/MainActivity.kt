package com.example.testmeteo.presentation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.testmeteo.R
import com.example.testmeteo.WeatherApplication
import com.example.testmeteo.data.local.WeatherDao
import com.example.testmeteo.network.WeatherApiService

class MainActivity: AppCompatActivity() {
    private lateinit var editTextCity: EditText
    private lateinit var btnGetWeather: Button
    private lateinit var textViewResult: TextView
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var weatherDao: WeatherDao

    private val apikey = "bd5e378503939ddaee76f12ad7a97688"


    private val sharedPreferencesName = "weatherSharedPref"
    private val sharedPreferenceskey = "last_api_call_date"

    private val apiService: WeatherApiService by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(WeatherApiService::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        editTextCity = findViewById(R.id.editTextCity)
        btnGetWeather = findViewById(R.id.btnGetWeather)
        textViewResult = findViewById(R.id.textViewResult)
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout)

        btnGetWeather.setOnClickListener {
            val city editTextCity.text.toString()
            if (city.isNotEmpty()) {
                fetchWeatherData(city)
            } else {
                Toast.makeText(context: this, text: "Please enter a city", Toast.LENGTH_SHORT).show()
            }
        }






    }

}