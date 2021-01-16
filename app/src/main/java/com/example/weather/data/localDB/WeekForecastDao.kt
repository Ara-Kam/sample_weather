package com.example.weather.data.localDB

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.weather.data.entity.WeekForecast

@Dao
interface WeekForecastDao {
    @Query("SELECT * FROM WeekForecast")
    fun getWeeklyForecast(): LiveData<WeekForecast>

    @Query("DELETE FROM WeekForecast")
    suspend fun deleteAll()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(dailyForecastList: WeekForecast)

    @Query("SELECT * FROM WeekForecast LIMIT 1")
    suspend fun getAnyRow(): List<WeekForecast>
}