package com.example.testmeteo.presentation

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.example.testmeteo.R
import com.example.testmeteo.WeatherApplication
import com.example.testmeteo.data.local.WeatherDao
import com.example.testmeteo.data.remote.Weather
import com.example.testmeteo.network.WeatherApiService
import com.example.weather.data.local.WeatherEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity: AppCompatActivity() {
    private lateinit var editTextCity: EditText
    private lateinit var btnGetWeather: Button
    private lateinit var textViewResult: TextView
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var weatherDao: WeatherDao

    private val apikey = "bd5e378503939ddaee76f12ad7a97608"


    private val sharedPreferencesName = "weatherSharedPref"
    private val sharedPreferencesKey = "last_api_call_date"

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
            val city = editTextCity.text.toString()
            if (city.isNotEmpty()) {
                fetchWeatherData(city)
            } else {
                Toast.makeText(this, "Please enter a city", Toast.LENGTH_SHORT).show()
            }
        }

        weatherDao = WeatherApplication.weatherDatabase.weatherDao()

    }

    private fun fetchWeatherData(city: String) {
        GlobalScope.launch(Dispatchers.Main) {

            val lastSavedWeather = getWeatherFromDatabase(city)
            swipeRefreshLayout.isRefreshing = true
            val call = apiService.getWeather(city, apikey)


            call.enqueue(object: Callback<Weather> {
                override fun onResponse(call: Call<Weather>, response: Response<Weather>) {
                    if (response.isSuccessful) {
                        val weather = response.body()
                        if (weather != null) {
                            saveLastApiCallDate()
                            launch(Dispatchers.IO) {
                                saveWeatherToDatabase(weather)
                            }
                            showWeatherData(weather)

                        } else {
                            showToast("Weather data is null")
                        }
                    } else {
                        showToast("Weather response error")
                    }
                    showLastApiCallDate()
                    swipeRefreshLayout.isRefreshing = false
                }


                override fun onFailure(call: Call<Weather>, throwable: Throwable) {
                    showToast("Exception: ${throwable.message}")
                    swipeRefreshLayout.isRefreshing = false
                }
            })
        }
    }

    private fun showToast(text: String) {
        Toast.makeText(this@MainActivity, text, Toast.LENGTH_SHORT)
            .show()
    }

    private fun showWeatherData(weather: Weather) {
        val resultText =
            "Temperature: ${weather.main.temp}\nHumidity: ${weather.main.humidity}\nDescription: ${weather.weather[0].description}"
        textViewResult.text = resultText
        // Afficher l'icône météo à partir de l'URL fournie par L'API
        val iconUrl = "https://openweathermap.org/img/wn/${weather.weather[0].icon}.png"
        Glide.with(this@MainActivity)
            .load(iconUrl)
            .into(findViewById(R.id.imageViewWeatherIcon))
    }

    private fun saveLastApiCallDate() {
        val currentDate =
            SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
        val sharedPreferences = getSharedPreferences (sharedPreferencesName, Context.MODE_PRIVATE)
        sharedPreferences.edit().putString(sharedPreferencesKey, currentDate).apply()
    }
    private fun showLastApiCallDate() {
        val sharedPreferences = getSharedPreferences(sharedPreferencesName, Context.MODE_PRIVATE)
        val lastApiCallDate = sharedPreferences.getString(sharedPreferencesKey, "")

        if (lastApiCallDate?.isNotEmpty() == true) {
            val formattedDate = "Last API Call: $lastApiCallDate"
            // Affiche la date dans le TextView
            findViewById<TextView>(R.id.textViewLastApiCallDate).text = formattedDate
        }
    }

    private suspend fun saveWeatherToDatabase (weather: Weather) {
        val weatherEntity = WeatherEntity(
            cityName = weather.name,
            temperature = weather.main.temp,
            humidity = weather.main.humidity,
            description = weather.weather[0].description,
            icon = weather.weather[0].icon
        )

        // Utilisation de Room pour insérer l'objet Weather dans la base de données
        withContext(Dispatchers.IO) {
            weatherDao.insert(weatherEntity)
        }
    }

    private suspend fun getWeatherFromDatabase(cityName: String): WeatherEntity? {

        return weatherDao.getLatestWeather(cityName)

    }





}