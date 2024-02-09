package com.example.testmeteo.presentation.view

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.testmeteo.R
import com.example.testmeteo.data.remote.Weather


class WeatherViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
    fun bind(weather: Weather) {
// Bind data to views
        val textViewTemperature = itemView.findViewById<TextView>(R.id.textViewTemperature)
        val textViewHumidity = itemView.findViewById<TextView>(R.id.textViewHumidity)
        val textViewDescription = itemView.findViewById<TextView>(R.id.textViewDescription)
        val imageViewIcon = itemView.findViewById<ImageView>(R.id.imageViewIcon)
        textViewTemperature.text = "Temperature: ${weather.main.temp}°C"
        textViewHumidity.text = "Humidity: ${weather.main.humidity}%"
        textViewDescription.text = "Description: ${weather.weather[0].description}"
// Load weather icon using Glide
        val iconUrl = "https://openweathermap.org/img/wn/${weather.weather.first().icon}.png"
        Glide.with(itemView.context)
            .load(iconUrl)
            .into(imageViewIcon)
    }
}