package com.example.weather.data.entity

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "CurrentForecast")
data class CurrentForecast(
    @PrimaryKey(autoGenerate = false) val id: Int,
    @ColumnInfo(name = "curr_lat") val lat: Float,
    @ColumnInfo(name = "curr_lon") val lon: Float,
    @ColumnInfo(name = "curr_timezone") val timezone: String,
    @Embedded val current: Current
)