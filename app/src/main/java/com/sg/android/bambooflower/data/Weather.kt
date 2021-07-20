package com.sg.android.bambooflower.data

data class Weather(
    val weatherImage: Int,
    val weather: String
) {
    companion object {
        const val SUNNY = "SUNNY"
        const val CLOUDY = "CLOUDY"
        const val RAINY = "RAINY"
        const val SNOWY = "SNOWY"
    }

    override fun equals(other: Any?): Boolean {
        other as Weather
        return weather == other.weather
    }
}