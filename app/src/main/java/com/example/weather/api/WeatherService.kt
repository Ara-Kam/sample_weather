package com.example.weather.api

import com.example.weather.data.entity.CityForecast
import com.example.weather.data.entity.CurrentForecast
import com.example.weather.data.entity.WeekForecast
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherService {
    // One Call API
    @GET("data/2.5/onecall?")
    suspend fun getWeeklyForecast(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") app_id: String,
        @Query("exclude") exclude: String? = null,
        @Query("units") units: String? = null,
        @Query("lang") lang: String? = null
    ): Response<WeekForecast>

    @GET("data/2.5/onecall?")
    suspend fun getCurrentForecast(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") app_id: String,
        @Query("units") units: String? = null,
        @Query("lang") lang: String? = null,
        @Query("exclude") exclude: String = "minutely,hourly,alerts,daily"
    ): Response<CurrentForecast>


    // Current weather data
    @GET("data/2.5/weather?")
    suspend fun getCurrWeatherByCityId(
        @Query("appid") app_id: String,
        @Query("id") city_id: Int,
        @Query("units") units: String = "metric",
        @Query("lang") lang: String = "en"
    ): Response<CityForecast>
}