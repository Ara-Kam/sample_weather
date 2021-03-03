package com.example.weather.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.example.weather.data.entity.DailyForecast
import com.example.weather.data.entity.WeekForecast
import com.example.weather.data.repository.WeatherForecastRepository
import com.example.weather.data.util.RemoteDataWrapper
import com.example.weather.location.LocationLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    var repository: WeatherForecastRepository,
    application: Application
) : AndroidViewModel(application) {

    private val locationData = LocationLiveData(application)
    fun getLocationData() = locationData

    private var mLat = 0F
    private var mLon = 0F
    private var mAppId = ""
    var isLocationFetched = false
        private set

    fun setRequiredParams(lat: Float, lon: Float, app_id: String) {
        mLat = lat
        mLon = lon
        mAppId = app_id
        isLocationFetched = true

        tryFetchingData()
    }

    private var retryCounter: Int = 0
    private var retryToFetchRemoteMutableLiveData: MutableLiveData<Int>? = null

    init {
        retryToFetchRemoteMutableLiveData = MutableLiveData<Int>()
    }

    fun tryFetchingData() {
        repository.getWeekForecastRemoteDataObj()
            .setQueryParams(mLat, mLon, mAppId)
        retryToFetchRemoteMutableLiveData?.value = retryCounter + 1
    }

    private val weekForecastListMutableLiveData =
        retryToFetchRemoteMutableLiveData?.switchMap { repository.getWeekForecast() }

    val weekForecast: LiveData<RemoteDataWrapper<WeekForecast>>?
        get() = weekForecastListMutableLiveData

    private val mSelectedWeekDayForecast by lazy { MutableLiveData<DailyForecast>() }

    fun setSelectedWeekDayForecast(selectedWeekDayForecast: DailyForecast) {
        mSelectedWeekDayForecast.value = selectedWeekDayForecast
    }

    fun getSelectedWeekDayForecast(): LiveData<DailyForecast> {
        return mSelectedWeekDayForecast
    }
}