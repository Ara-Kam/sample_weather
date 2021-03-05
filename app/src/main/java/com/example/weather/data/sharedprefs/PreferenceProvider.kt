package com.example.weather.data.sharedprefs

import android.content.Context
import android.content.SharedPreferences
import com.example.weather.location.MyLocationModel
import com.example.weather.location.jsonToMyLocation
import com.example.weather.location.myLocationToJson

class PreferenceProvider constructor(context: Context) {

    companion object {
        private const val SELECTED_MEASUREMENT_UNIT = "SELECTED_MEASUREMENT_UNIT"
        private const val PREFS_FILE_NAME = "my_prefs"
        private const val LAST_KNOWN_LOCATION = "last_known_location"
    }

    private var mPrefs: SharedPreferences

    init {
        mPrefs = context.getSharedPreferences(PREFS_FILE_NAME, Context.MODE_PRIVATE)
    }

    var measurementUnit: String
        get() = mPrefs.getString(SELECTED_MEASUREMENT_UNIT, "metric").toString()
        set(value) = mPrefs.edit().putString(SELECTED_MEASUREMENT_UNIT, value).apply()

    fun getTemperatureUnit(): String {
        return when (measurementUnit) {
            "metric" -> "\u2103"    // Celsius
            "imperial" -> "\u2109"  // Fahrenheit
            else -> "K"             // Kelvin
        }
    }

    fun getLastKnownLocation(): MyLocationModel =
        jsonToMyLocation(
            mPrefs.getString(LAST_KNOWN_LOCATION, "{longitude:40.177568, latitude:44.512587}")
                .toString()
        )

    fun setLastKnownLocation(lastLocation: MyLocationModel) =
        mPrefs.edit().putString(LAST_KNOWN_LOCATION, myLocationToJson(lastLocation)).apply()
}