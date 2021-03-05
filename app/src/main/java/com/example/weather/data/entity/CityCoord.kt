package com.example.weather.data.entity

import androidx.room.ColumnInfo

data class CityCoord(
    @ColumnInfo(name = "city_lon") val lon: Double,
    @ColumnInfo(name = "city_lat") val lat: Double
)
