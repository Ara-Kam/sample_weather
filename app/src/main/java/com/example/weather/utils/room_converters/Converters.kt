package com.example.weather.utils.room_converters

import androidx.room.TypeConverter
import com.example.weather.data.entity.DailyForecast
import com.example.weather.data.entity.Weather
import com.google.gson.Gson

class Converters {
    @TypeConverter
    fun listToJson(value: List<DailyForecast>?) = Gson().toJson(value)

    @TypeConverter
    fun jsonToList(value: String) =
        Gson().fromJson(value, Array<DailyForecast>::class.java).toList()

    @TypeConverter
    fun weatherListToJson(value: List<Weather>?) = Gson().toJson(value)

    @TypeConverter
    fun jsonToWeatherList(value: String) =
        Gson().fromJson(value, Array<Weather>::class.java).toList()
}