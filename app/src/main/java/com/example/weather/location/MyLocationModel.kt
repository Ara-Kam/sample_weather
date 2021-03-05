package com.example.weather.location

import java.io.Serializable

data class MyLocationModel(
    val longitude: Double,
    val latitude: Double
) : Serializable