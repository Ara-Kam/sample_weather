package com.example.weather.data.localDB

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.weather.data.entity.CityForecast

@Dao
interface CitiesForecastDao {
    @Query("SELECT * FROM CityForecast")
    fun getCitiesForecast(): LiveData<List<CityForecast>>

    @Query("DELETE FROM CityForecast")
    suspend fun deleteAll()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(citiesForecastList: CityForecast)

    @Query("SELECT * FROM CityForecast LIMIT 1")
    suspend fun getAnyRow(): List<CityForecast>
}