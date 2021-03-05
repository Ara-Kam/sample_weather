package com.example.weather.data.repository

import android.content.Context
import com.example.weather.data.localDB.CitiesForecastDao
import com.example.weather.data.util.FetchDataStrategy
import javax.inject.Inject

class CityForecastRepository @Inject constructor(
    private val cityForecastRemoteData: CityForecastRemoteData,
    private val cityForecastDao: CitiesForecastDao
) {
    fun getCitiesForecast(context: Context) = FetchDataStrategy.fetchCitiesForecastData(
        getCities = { cityForecastRemoteData.getCities(context) },
        networkCall = { cityForecastRemoteData.getCityForecast(it) },
        databaseQuery = { cityForecastDao.getCitiesForecast() },
        getAnyFromDb = { cityForecastDao.getAnyRow() },
        saveCallResult = { cityForecastDao.insert(it) }
    )
}