package com.example.weather.data.util

data class RemoteDataWrapper<out T>(val status: Status, val data: T?, val message: String?) {

    enum class Status {
        SUCCESS,
        ERROR,
        LOADING
    }

    companion object {
        fun <T> success(data: T): RemoteDataWrapper<T> {
            return RemoteDataWrapper(Status.SUCCESS, data, null)
        }

        fun <T> error(message: String, data: T? = null): RemoteDataWrapper<T> {
            return RemoteDataWrapper(Status.ERROR, data, message)
        }

        fun <T> loading(data: T? = null): RemoteDataWrapper<T> {
            return RemoteDataWrapper(Status.LOADING, data, null)
        }
    }
}