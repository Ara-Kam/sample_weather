package com.example.weather.data.entity

import androidx.room.ColumnInfo

data class Current(
    @ColumnInfo(name = "curr_dt") val dt: Long,
    val sunrise: Long,
    val sunset: Long,
    @ColumnInfo(name = "curr_temp") val temp: Float,
    val feels_like: Float,
    val pressure: Float,
    val humidity: Float,
    val dew_point: Float,
    val uvi: Float,
    val clouds: Float,
    val visibility: Float,
    val wind_speed: Float,
    val wind_deg: Float,
    val weather: List<Weather>
)