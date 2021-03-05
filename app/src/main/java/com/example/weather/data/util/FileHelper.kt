package com.example.weather.data.util

import android.content.Context
import com.example.weather.data.entity.City
import com.google.gson.Gson
import timber.log.Timber
import java.io.IOException

class FileHelper {
    companion object {
        fun readJsonFromAssets(context: Context, fileName: String): List<City>? {
            val json: String
            try {
                val inputStream = context.assets.open(fileName)
                val size = inputStream.available()
                val buffer = ByteArray(size)
                inputStream.use { it.read(buffer) }
                json = String(buffer)
            } catch (ioException: IOException) {
                Timber.e(ioException.localizedMessage)
                return null
            }
            return Gson().fromJson(json, Array<City>::class.java).toList()
        }
    }
}