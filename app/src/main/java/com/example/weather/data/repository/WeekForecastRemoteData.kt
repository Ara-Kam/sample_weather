package com.example.weather.data.repository

import com.example.weather.BuildConfig
import com.example.weather.api.WeatherService
import com.example.weather.data.sharedprefs.PreferenceProvider
import com.example.weather.data.util.RemoteData
import javax.inject.Inject

class WeekForecastRemoteData @Inject constructor(
    private val apiClient: WeatherService,
) : RemoteData() {

    @Inject
    lateinit var mSharedPrefs: PreferenceProvider

    suspend fun getRemoteWeekForecast(lat: Double, lon: Double) =
        getRemoteData {
            apiClient.getWeeklyForecast(
                lat,
                lon,
                BuildConfig.API_ID,
                "current,minutely,hourly,alerts",
                mSharedPrefs.measurementUnit,
                "en"
            )
        }

    suspend fun getCurrentForecast() =
        getRemoteData {
            apiClient.getCurrentForecast(
                mSharedPrefs.getLastKnownLocation().latitude,
                mSharedPrefs.getLastKnownLocation().longitude,
                BuildConfig.API_ID,
                mSharedPrefs.measurementUnit,
                "en",
                "minutely,hourly,alerts,daily"
            )
        }
}