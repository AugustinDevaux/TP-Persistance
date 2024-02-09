package com.example.testmeteo.presentation

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Adapter
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.example.testmeteo.R
import com.example.testmeteo.WeatherApplication
import com.example.testmeteo.data.local.WeatherDao
import com.example.testmeteo.data.remote.Weather
import com.example.testmeteo.network.WeatherApiService
import com.example.testmeteo.presentation.view.WeatherAdapter
import com.example.weather.data.local.WeatherEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
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
    private lateinit var recyclerView: RecyclerView
    private lateinit var weatherDao: WeatherDao
    private lateinit var adapter: WeatherAdapter

    private val viewModel = MainViewModel()

    private val apiKey = "bd5e378503939ddaee76f12ad7a97608"

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
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout)

        //Ajout des listeners
        btnGetWeather.setOnClickListener { fetchData() }
        swipeRefreshLayout.setOnRefreshListener { fetchData() }

        //Init du weatherDao
        weatherDao = WeatherApplication.weatherDatabase.weatherDao()

        //Init weatherAdapter
        adapter = WeatherAdapter()

        recyclerView = findViewById(R.id.recyclerViewWeather)
        recyclerView.layoutManager =
            LinearLayoutManager(
                this@MainActivity,
                LinearLayoutManager.VERTICAL,
                false
            )
        recyclerView.adapter = adapter
    }


    private fun fetchData() {
        val cities = viewModel.splitCitiesNames(editTextCity.text.toString())
        if (cities.isNotEmpty()) {
            runBlocking {
            fetchWeatherData(cities)
            }
        } else {
            Toast.makeText(this, "Please enter a city", Toast.LENGTH_SHORT).show()
        }
    }

    private suspend fun fetchWeatherData(cities: List<String>) {
        swipeRefreshLayout.isRefreshing = true
        coroutineScope {
            // Utiliser async pour lancer plusieurs coroutines en parallèle
            val deferredWeather = cities.map { city ->
                async(Dispatchers.IO) {
                    // Faire un appel à l'API pour chaque ville
                    val response = apiService.getWeather(city, apiKey).execute()
                    if (response.isSuccessful) {
                        // Sauvegarde la date du dernier appel à l'API dans SharedPreferences
                        saveLastApiCallDate()
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
            weathers.map { weather ->
                if (weather != null) {
                 // Sauvegarde l'objet Weather dans la BDD Room
                    launch(Dispatchers.IO) {
                        saveWeatherToDatabase(weather)
                    }
                    nonEmptyWeathers.apply {
                        this.add(weather)
                    }
                }
            }
            if (nonEmptyWeathers.isNotEmpty()) {
                // Afficher les données météorologiques dans l'interface utilisateur
                showWeatherData(nonEmptyWeathers)
                showLastApiCallDate()
            } else {
                showToast("Failed to fetch weather data")
            }

            swipeRefreshLayout.isRefreshing = false
        }
    }

    private fun showWeatherData(weathers: List<Weather>) {
        adapter.setData(weathers)
    }

    private fun showToast(text: String) {
        Toast.makeText(this@MainActivity, text, Toast.LENGTH_SHORT)
            .show()
    }

    //OLD showWeatherData
/*    private fun showWeatherData(weather: Weather) {
        val resultText =
            "Temperature: ${weather.main.temp}\nHumidity: ${weather.main.humidity}\nDescription: ${weather.weather[0].description}"
        textViewResult.text = resultText
        // Afficher l'icône météo à partir de l'URL fournie par L'API
        val iconUrl = "https://openweathermap.org/img/wn/${weather.weather[0].icon}.png"
        Glide.with(this@MainActivity)
            .load(iconUrl)
            .into(findViewById(R.id.imageViewWeatherIcon))
    }*/

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