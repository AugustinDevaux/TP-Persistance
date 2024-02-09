package com.example.testmeteo.presentation.view

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.testmeteo.R
import com.example.testmeteo.data.remote.Weather

class WeatherAdapter: RecyclerView.Adapter<WeatherViewHolder>() {

    private var weatherList: MutableList<Weather> = arrayListOf()
    override fun onCreateViewHolder (parent: ViewGroup, viewType: Int): WeatherViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_weather, parent, false)
        return WeatherViewHolder(view)
    }

    override fun onBindViewHolder (holder: WeatherViewHolder, position: Int) {
        val city = weatherList [position]
        holder.bind(city)
    }

    override fun getItemCount(): Int {
        return weatherList.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setData(weatherList: List<Weather>) {
        this.weatherList.apply {
            addAll(weatherList)
            notifyDataSetChanged()
        }
    }
}