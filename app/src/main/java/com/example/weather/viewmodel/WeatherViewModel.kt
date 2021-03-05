package com.example.weather.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.example.weather.data.entity.CityForecast
import com.example.weather.data.entity.CurrentForecast
import com.example.weather.data.entity.DailyForecast
import com.example.weather.data.entity.WeekForecast
import com.example.weather.data.repository.CityForecastRepository
import com.example.weather.data.repository.WeatherForecastRepository
import com.example.weather.data.sharedprefs.PreferenceProvider
import com.example.weather.data.util.RemoteDataWrapper
import com.example.weather.location.LocationLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    val repository: WeatherForecastRepository,
    private val cityForecastRepository: CityForecastRepository,
    application: Application
) : AndroidViewModel(application) {

    private val mContext = application

    @Inject
    lateinit var mSharedPrefs: PreferenceProvider

    private val locationData = LocationLiveData(application)
    fun getLocationData() = locationData

    var isLocationFetched = false
        private set

    fun fetchCurrentForecast() {
        isLocationFetched = true
        getCurrentForecast()
    }

    var weekForecastMutableResult = MutableLiveData<RemoteDataWrapper<WeekForecast>>()
    private lateinit var _weekForecastResult: LiveData<RemoteDataWrapper<WeekForecast>>
    fun getWeekForecast(lat: Double, lon: Double) {
        _weekForecastResult = repository.getWeekForecast(lat, lon)
        _weekForecastResult.observeForever { weekForecastMutableResult.value = it }
    }

    val weekForecastResult: LiveData<RemoteDataWrapper<WeekForecast>>
        get() = weekForecastMutableResult

    private val mSelectedWeekDayForecast by lazy { MutableLiveData<DailyForecast>() }

    fun setSelectedWeekDayForecast(selectedWeekDayForecast: DailyForecast) {
        mSelectedWeekDayForecast.value = selectedWeekDayForecast
    }

    fun getSelectedWeekDayForecast(): LiveData<DailyForecast> {
        return mSelectedWeekDayForecast
    }

    // Cities forecast screen
    private lateinit var _currentForecastResult: LiveData<RemoteDataWrapper<CurrentForecast>>
    private var _currentForecast = MutableLiveData<RemoteDataWrapper<CurrentForecast>>()
    fun getCurrentForecast() {
        _currentForecastResult = repository.getCurrentForecast()
        _currentForecastResult.observeForever { _currentForecast.value = it }
    }

    val currentForecastResult: LiveData<RemoteDataWrapper<CurrentForecast>>
        get() = _currentForecast

    // Get cities forecast
    private lateinit var _citiesForecastResult: LiveData<RemoteDataWrapper<List<CityForecast>>>
    private var citiesForecastMutableResult =
        MutableLiveData<RemoteDataWrapper<List<CityForecast>>>()

    fun getCitiesForecast() {
        _citiesForecastResult = cityForecastRepository.getCitiesForecast(context = mContext)
        _citiesForecastResult.observeForever { citiesForecastMutableResult.value = it }
    }

    val citiesForecastResult: LiveData<RemoteDataWrapper<List<CityForecast>>>
        get() = citiesForecastMutableResult

    override fun onCleared() {
        if (this::_currentForecastResult.isInitialized) {
            _currentForecastResult.removeObserver { _currentForecast }
        }

        if (this::_weekForecastResult.isInitialized) {
            _weekForecastResult.removeObserver { weekForecastMutableResult }
        }

        if (this::_citiesForecastResult.isInitialized) {
            _citiesForecastResult.removeObserver { citiesForecastMutableResult }
        }

        super.onCleared()
    }
}