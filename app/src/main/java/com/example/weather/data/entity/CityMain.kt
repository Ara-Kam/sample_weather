package com.example.weather.data.entity

import androidx.room.ColumnInfo

data class CityMain(
    @ColumnInfo(name = "city_main_temp") val temp: Float,
    @ColumnInfo(name = "city_main_feels_like") val feels_like: Float,
    @ColumnInfo(name = "city_main_temp_min") val temp_min: Float,
    @ColumnInfo(name = "city_main_temp_max") val temp_max: Float,
    @ColumnInfo(name = "city_main_pressure") val pressure: Float,
    @ColumnInfo(name = "city_main_humidity") val humidity: Float
)
