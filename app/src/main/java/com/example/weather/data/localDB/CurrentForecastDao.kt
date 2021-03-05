package com.example.weather.data.localDB

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.weather.data.entity.CurrentForecast

@Dao
interface CurrentForecastDao {
    @Query("SELECT * FROM CurrentForecast")
    fun getCurrentForecast(): LiveData<CurrentForecast>

    @Query("DELETE FROM CurrentForecast")
    suspend fun deleteAll()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(dailyForecastList: CurrentForecast)

    @Query("SELECT * FROM CurrentForecast LIMIT 1")
    suspend fun getAnyRow(): List<CurrentForecast>
}