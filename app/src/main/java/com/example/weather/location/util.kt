package com.example.weather.location

import com.google.gson.Gson

const val LOCATION_PERMISSION_REQUEST = 0
const val REQUEST_CHECK_SETTINGS = 1

fun myLocationToJson(myLocation: MyLocationModel): String {
    val gson = Gson()
    return gson.toJson(myLocation)
}

fun jsonToMyLocation(json: String): MyLocationModel {
    val gson = Gson()
    return gson.fromJson(json, MyLocationModel::class.java)
}