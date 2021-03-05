package com.example.weather.data.repository

import com.example.weather.data.localDB.CurrentForecastDao
import com.example.weather.data.util.FetchDataStrategy
import javax.inject.Inject

class WeatherForecastRepository @Inject constructor(
    private val currentForecastDao: CurrentForecastDao,
    private val weatherRemoteData: WeekForecastRemoteData,
) {
    fun getWeekForecast(lat: Double, lon: Double) = FetchDataStrategy.fetchWeekForecastData(
        networkCall = { weatherRemoteData.getRemoteWeekForecast(lat, lon) }
    )

    fun getCurrentForecast() = FetchDataStrategy.fetchCurrentForecastData(
        databaseQuery = { currentForecastDao.getCurrentForecast() },
        networkCall = { weatherRemoteData.getCurrentForecast() },
        getAnyFromDb = { currentForecastDao.getAnyRow() },
        saveCallResult = { currentForecastDao.insert(it) }
    )
}