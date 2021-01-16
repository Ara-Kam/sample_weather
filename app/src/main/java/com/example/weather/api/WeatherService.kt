package com.example.weather.api

import com.example.weather.data.entity.WeekForecast
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherService {
    @GET("data/2.5/onecall?")
    suspend fun getWeeklyForecast(
        @Query("lat") lat: Float,
        @Query("lon") lon: Float,
        @Query("appid") app_id: String,
        @Query("exclude") exclude: String? = null,
        @Query("units") units: String? = null,
        @Query("units") lang: String? = null
    ): Response<WeekForecast>
}