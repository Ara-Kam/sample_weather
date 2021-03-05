package com.example.weather.data.repository

import android.content.Context
import com.example.weather.BuildConfig
import com.example.weather.api.WeatherService
import com.example.weather.data.entity.City
import com.example.weather.data.util.FileHelper
import com.example.weather.data.util.RemoteData
import javax.inject.Inject

class CityForecastRemoteData @Inject constructor(
    private val apiClient: WeatherService,
) : RemoteData() {

    suspend fun getCityForecast(city: City?) =
        getRemoteData {
            city?.let {
                apiClient.getCurrWeatherByCityId(
                    BuildConfig.API_ID,
                    it.id
                )
            }
        }

    fun getCities(context: Context) = FileHelper.readJsonFromAssets(context, "cities.json")
}