package com.example.weather.data.repository

import com.example.weather.api.WeatherService
import com.example.weather.data.sharedprefs.PreferenceProvider
import com.example.weather.data.util.RemoteData
import javax.inject.Inject

class WeekForecastRemoteData @Inject constructor(
    private val apiClient: WeatherService,
) : RemoteData() {

    @Inject
    lateinit var mSharedPrefs: PreferenceProvider

    var mLat: Float = 0F
    private var mLon: Float = 0F
    private var mApp_Id: String = ""
    private var mExclude: String = ""
    private var mUnits: String = ""
    private var mLang: String = ""

    fun setQueryParams(
        lat: Float,
        lon: Float,
        app_id: String,
        exclude: String = "current,minutely,hourly,alerts",
        units: String = "metric",
        lang: String = "en"
    ) {
        mLat = lat
        mLon = lon
        mApp_Id = app_id
        mExclude = exclude
        mUnits = units
        mLang = lang

        mSharedPrefs.measurementUnit = mUnits
    }

    suspend fun getRemoteWeekForecast() =
        getRemoteData { apiClient.getWeeklyForecast(mLat, mLon, mApp_Id, mExclude, mUnits, mLang) }
}