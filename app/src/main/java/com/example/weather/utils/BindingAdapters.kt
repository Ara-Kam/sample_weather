package com.example.weather.utils

import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.example.weather.R
import com.squareup.picasso.Picasso

@BindingAdapter("timestamp")
fun setDate(view: TextView, timestamp: Long) {
    view.text = DateConverter.getDateTime(timestamp)
}

@BindingAdapter("weatherIcon")
fun bindWeatherIcon(imgView: ImageView, iconUri: String?) {
    Picasso.get().load(iconUri)
        .placeholder(R.drawable.weather_placeholder_ic)
        .resize(
            LayoutHelper.pixels(imgView.context, 60F),
            LayoutHelper.pixels(imgView.context, 60F)
        )
        .error(android.R.drawable.stat_notify_error)
        .into(imgView)
}