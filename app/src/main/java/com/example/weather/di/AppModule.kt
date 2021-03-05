package com.example.weather.di

import android.content.Context
import com.example.weather.BuildConfig
import com.example.weather.api.WeatherService
import com.example.weather.data.localDB.LocalDatabase
import com.example.weather.data.sharedprefs.PreferenceProvider
import com.google.android.gms.location.LocationServices
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    fun provideGson(): Gson = GsonBuilder().create()

    @Provides
    fun provideRetrofit(gson: Gson): Retrofit = Retrofit.Builder()
        .baseUrl(BuildConfig.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()

    @Provides
    fun provideDatabase(@ApplicationContext appContext: Context) =
        LocalDatabase.getDatabase(appContext)

    @Provides
    fun provideFusedLocationProviderClient(@ApplicationContext appContext: Context) =
        LocationServices.getFusedLocationProviderClient(appContext)

    @Provides
    fun provideWeekForecastDao(localDatabase: LocalDatabase) = localDatabase.currentForecastDao()

    @Provides
    fun provideCitiesForecastDao(localDatabase: LocalDatabase) = localDatabase.citiesForecastDao()

    @Provides
    fun provideWeatherService(retrofit: Retrofit): WeatherService =
        retrofit.create(WeatherService::class.java)

    @Provides
    fun providePreferences(@ApplicationContext appContext: Context) =
        PreferenceProvider(appContext)
}