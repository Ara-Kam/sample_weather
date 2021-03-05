package com.example.weather.data.entity

import androidx.room.*
import com.example.weather.utils.room_converters.Converters

@Entity(tableName = "CityForecast")
data class CityForecast(
    @PrimaryKey(autoGenerate = false) val id: Int,
    @Embedded val coord: CityCoord,
    @ColumnInfo(name = "city_name") val name: String,
    @ColumnInfo(name = "city_timezone") val timezone: String,
    @ColumnInfo(name = "city_dt") val dt: Long,
    @TypeConverters(Converters::class) val weather: List<Weather>,
    @Embedded val main: CityMain
)
