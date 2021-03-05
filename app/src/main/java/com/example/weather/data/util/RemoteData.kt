package com.example.weather.data.util

import retrofit2.Response

abstract class RemoteData {
    protected suspend fun <T> getRemoteData(call: suspend () -> Response<T>?): RemoteDataWrapper<T> {
        try {
            val response = call() ?: throw Exception()
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) return RemoteDataWrapper.success(body)
            }
            return error("${response.code()} ${response.message()}")
        } catch (e: Exception) {
            return error(e.message ?: e.toString())
        }
    }

    private fun <T> error(message: String): RemoteDataWrapper<T> {
        return RemoteDataWrapper.error("Failed! \n$message")
    }
}