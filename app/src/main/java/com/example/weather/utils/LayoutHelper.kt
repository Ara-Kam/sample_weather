package com.example.weather.utils

import android.content.Context
import android.util.TypedValue

class LayoutHelper {
    companion object {
        fun pixels(context: Context, dip: Float): Int {
            return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, dip,
                context.resources.displayMetrics
            ).toInt()
        }

        fun dips(context: Context, pixel: Int): Int {
            return (pixel / context.resources.displayMetrics.density).toInt()
        }
    }
}