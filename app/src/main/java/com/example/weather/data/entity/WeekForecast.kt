package com.example.weather.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.weather.utils.room_converters.Converters
import java.io.Serializable

@Entity(tableName = "WeekForecast")
data class WeekForecast(
    @PrimaryKey(autoGenerate = false) val id: Int,
    val lat: Float,
    val lon: Float,
    val timezone: String,
    val timezone_offset: Long,
    @TypeConverters(Converters::class)
    val daily: List<DailyForecast>
): Serializable