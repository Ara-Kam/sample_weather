package com.example.weather.data.sharedprefs

import android.content.Context
import android.content.SharedPreferences

class PreferenceProvider constructor(context: Context) {

    private val SELECTED_MEASUREMENT_UNIT = "SELECTED_MEASUREMENT_UNIT"
    private val PREFS_FILE_NAME = "my_prefs"

    private var mPrefs: SharedPreferences

    init {
        mPrefs = context.getSharedPreferences(PREFS_FILE_NAME, Context.MODE_PRIVATE)
    }

    var measurementUnit: String
        get() = mPrefs.getString(SELECTED_MEASUREMENT_UNIT, "standard").toString()
        set(value) = mPrefs.edit().putString(SELECTED_MEASUREMENT_UNIT, value).apply()

    fun getTemperatureUnit(): String {
        return when (measurementUnit) {
            "metric" -> "\u2103"    // Celsius
            "imperial" -> "\u2109"  // Fahrenheit
            else -> "K"             // Kelvin
        }
    }
}