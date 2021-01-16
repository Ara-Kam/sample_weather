package com.example.weather.data.entity

import java.io.Serializable

data class DailyForecast(
    val dt: Long,
    val sunrise: Long,
    val sunset: Long,
    val temp: Temp,
    val feels_like: FeelsLike,
    val pressure: Int,
    val humidity: Int,
    val dew_point: Float,
    val wind_speed: Float,
    val wind_deg: Float,
    var weather: List<Weather>,
    val clouds: Int,
    val pop: Float,
    val uvi: Float
) : Serializable