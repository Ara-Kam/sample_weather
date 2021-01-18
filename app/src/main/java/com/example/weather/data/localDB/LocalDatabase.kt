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
    entities = [WeekForecast::class],
    version = 2,
    exportSchema = false
)

abstract class LocalDatabase : RoomDatabase() {
    abstract fun dailyForecastDao(): WeekForecastDao

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
            Room.databaseBuilder(appContext, LocalDatabase::class.java, "WeekForecastDB")
                .fallbackToDestructiveMigration()
                .build()
    }
}