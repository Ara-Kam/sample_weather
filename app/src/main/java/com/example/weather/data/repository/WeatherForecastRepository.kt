package com.example.weather.data.repository

import com.example.weather.data.localDB.WeekForecastDao
import com.example.weather.data.util.FetchDataStrategy
import javax.inject.Inject

class WeatherForecastRepository @Inject constructor(
    private val weekForecastDao: WeekForecastDao,
    private val weatherRemoteData: WeekForecastRemoteData,
) {
    fun getWeekForecast() = FetchDataStrategy.fetchWeekForecastData(
        databaseQuery = { weekForecastDao.getWeeklyForecast() },
        networkCall = { weatherRemoteData.getRemoteWeekForecast() },
        deleteLocalDb = {weekForecastDao.deleteAll()},
        getAnyFromDb = {weekForecastDao.getAnyRow()},
        saveCallResult = { weekForecastDao.insertAll(it) }
    )

    fun getWeekForecastRemoteDataObj() = weatherRemoteData
}