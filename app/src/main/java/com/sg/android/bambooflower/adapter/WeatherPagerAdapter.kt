package com.sg.android.bambooflower.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sg.android.bambooflower.R
import com.sg.android.bambooflower.adapter.viewholder.WeatherViewHolder
import com.sg.android.bambooflower.data.Weather
import com.sg.android.bambooflower.databinding.PagerWeatherBinding

class WeatherPagerAdapter() : RecyclerView.Adapter<WeatherViewHolder>() {
    private val imageResources = listOf(
        Weather(R.drawable.test_image, Weather.SUNNY),
        Weather(R.drawable.test_image, Weather.CLOUDY),
        Weather(R.drawable.test_image, Weather.RAINY),
        Weather(R.drawable.test_image, Weather.SNOWY)
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeatherViewHolder {
        val view =
            PagerWeatherBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return WeatherViewHolder(view)
    }

    override fun onBindViewHolder(holder: WeatherViewHolder, position: Int) {
        holder.bind(imageResources[position].weatherImage)
    }

    override fun getItemCount(): Int {
        return imageResources.size
    }

    fun getItem(pos: Int) =
        imageResources[pos]

    fun getIndex(weather: Weather) =
        imageResources.indexOf(weather)
}