package com.sg.android.bambooflower.adapter.viewholder

import androidx.recyclerview.widget.RecyclerView
import com.sg.android.bambooflower.databinding.PagerWeatherBinding

class WeatherViewHolder(private val binding: PagerWeatherBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(imageRes: Int) {
        binding.imageRes = imageRes
    }
}