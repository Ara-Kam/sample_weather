package com.example.weather.data.util

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.map
import kotlinx.coroutines.Dispatchers

class FetchDataStrategy {

    companion object {
        fun <T> fetchWeekForecastData(
            networkCall: suspend () -> RemoteDataWrapper<T>,
        ): LiveData<RemoteDataWrapper<T>> =
            liveData(Dispatchers.IO) {
                emit(RemoteDataWrapper.loading())
                val remoteResponseStatus = networkCall.invoke()
                if (remoteResponseStatus.status == RemoteDataWrapper.Status.SUCCESS) {
                    emit(RemoteDataWrapper.success(remoteResponseStatus.data!!))
                } else if (remoteResponseStatus.status == RemoteDataWrapper.Status.ERROR) {
                    emit(RemoteDataWrapper.error(remoteResponseStatus.message!!))
                    return@liveData
                }
            }

        fun <T, A> fetchCurrentForecastData(
            databaseQuery: () -> LiveData<T>,
            networkCall: suspend () -> RemoteDataWrapper<A>,
            getAnyFromDb: suspend () -> List<*>,
            saveCallResult: suspend (A) -> Unit,
        ): LiveData<RemoteDataWrapper<T>> =
            liveData(Dispatchers.IO) {
                emit(RemoteDataWrapper.loading())

                val remoteResponseStatus = networkCall.invoke()
                if (remoteResponseStatus.status == RemoteDataWrapper.Status.SUCCESS) {
                    // Rewrite a new data in DB.
                    saveCallResult(remoteResponseStatus.data!!)

                    emitSource(databaseQuery.invoke().map { RemoteDataWrapper.success(it) })
                } else if (remoteResponseStatus.status == RemoteDataWrapper.Status.ERROR) {
                    //  Check if local DB is empty
                    val localDataSourceValue = getAnyFromDb.invoke()
                    if (localDataSourceValue.isEmpty()) {
                        emit(RemoteDataWrapper.error(remoteResponseStatus.message!!))
                        return@liveData
                    }
                    emitSource(databaseQuery.invoke().map { RemoteDataWrapper.success(it) })
                }
            }

        fun <T, A, S> fetchCitiesForecastData(
            getCities: () -> List<S>?,
            networkCall: suspend (S?) -> RemoteDataWrapper<A>,
            databaseQuery: () -> LiveData<T>,
            getAnyFromDb: suspend () -> List<*>,
            saveCallResult: suspend (A) -> Unit,
        ): LiveData<RemoteDataWrapper<T>> =
            liveData(Dispatchers.IO) {
                emit(RemoteDataWrapper.loading())

                // Get cities from assets json file
                val cities = getCities()
                val remoteResponseStatuses = cities?.map { city -> networkCall(city) }
                remoteResponseStatuses?.map { remoteResponseStatus ->
                    when (remoteResponseStatus.status) {
                        RemoteDataWrapper.Status.SUCCESS -> {
                            // Rewrite a new data in DB.
                            saveCallResult(remoteResponseStatus.data!!)
                            emitSource(databaseQuery.invoke().map { RemoteDataWrapper.success(it) })
                        }
                        else -> {
                            //  Check if local DB is empty
                            val localDataSourceValue = getAnyFromDb.invoke()
                            if (localDataSourceValue.isEmpty()) {
                                emit(RemoteDataWrapper.error(remoteResponseStatus.message!!))
                                return@liveData
                            }
                            emitSource(databaseQuery.invoke().map { RemoteDataWrapper.success(it) })
                        }
                    }
                }
            }
    }
}