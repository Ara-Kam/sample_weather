package com.example.weather.utils

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.*

class DateConverter {
    companion object{
        @SuppressLint("SimpleDateFormat")
        fun getDateTime(s: String): String? {
            return try {
                val sdf = SimpleDateFormat("EE, MMM d")
                val netDate = Date(s.toLong() * 1000)
                sdf.format(netDate)
            } catch (e: Exception) {
                e.toString()
            }
        }
    }
}