package com.example.weather.data.entity

import androidx.room.TypeConverters
import com.example.weather.utils.room_converters.Converters
import java.io.Serializable

data class WeekForecast(
    val lat: Float,
    val lon: Float,
    val timezone: String,
    val timezone_offset: Long,
    @TypeConverters(Converters::class)
    val daily: List<DailyForecast>
): Serializable