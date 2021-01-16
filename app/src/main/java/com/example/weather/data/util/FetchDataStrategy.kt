package com.example.weather.data.util

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.map
import kotlinx.coroutines.Dispatchers

class FetchDataStrategy {

    companion object {
        fun <T, A> fetchWeekForecastData(
            databaseQuery: () -> LiveData<T>,
            networkCall: suspend () -> RemoteDataWrapper<A>,
            deleteLocalDb: suspend () -> Unit,
            getAnyFromDb: suspend () -> List<*>,
            saveCallResult: suspend (A) -> Unit,
        ): LiveData<RemoteDataWrapper<T>> =
            liveData(Dispatchers.IO) {
                emit(RemoteDataWrapper.loading())

                val remoteResponseStatus = networkCall.invoke()
                if (remoteResponseStatus.status == RemoteDataWrapper.Status.SUCCESS) {
                    // Rewrite a new data in DB.
                    deleteLocalDb.invoke()
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
    }
}