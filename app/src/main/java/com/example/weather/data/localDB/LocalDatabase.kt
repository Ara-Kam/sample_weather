package com.example.weather.data.localDB

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.weather.data.entity.*
import com.example.weather.utils.room_converters.Converters

@TypeConverters(Converters::class)
@Database(
    entities = [CurrentForecast::class, CityForecast::class],
    version = 3,
    exportSchema = true
)

abstract class LocalDatabase : RoomDatabase() {
    abstract fun currentForecastDao(): CurrentForecastDao
    abstract fun citiesForecastDao(): CitiesForecastDao

    companion object {
        @Volatile
        private var instance: LocalDatabase? = null

        fun getDatabase(context: Context): LocalDatabase =
            instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also {
                    instance = it
                }
            }

        private fun buildDatabase(appContext: Context) =
            Room.databaseBuilder(appContext, LocalDatabase::class.java, "WeatherForecastDB")
                .fallbackToDestructiveMigration()
                .build()
    }
}