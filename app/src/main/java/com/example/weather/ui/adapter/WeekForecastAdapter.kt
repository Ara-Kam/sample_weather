package com.example.weather.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.weather.BuildConfig
import com.example.weather.R
import com.example.weather.data.entity.DailyForecast
import com.example.weather.databinding.ItemDayForecastViewBinding
import com.example.weather.listener.ItemClickListener
import com.example.weather.utils.DateConverter
import com.example.weather.utils.LayoutHelper
import com.squareup.picasso.Picasso
import javax.inject.Inject
import kotlin.math.roundToInt

class WeekForecastAdapter @Inject constructor() :
    RecyclerView.Adapter<WeekForecastAdapter.DailyForecastViewHolder>() {

    var mOnItemClickListener: ItemClickListener<DailyForecast>? = null
    private var mDailyForecastList = emptyList<DailyForecast>()
    private var mTempMeasurementUnitSign = ""

    class DailyForecastViewHolder(private val itemBinding: ItemDayForecastViewBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(
            dailyForecast: DailyForecast,
            mTempMeasurementUnitSign: String,
            mOnItemClickListener: ItemClickListener<DailyForecast>?
        ) {
            itemBinding.root.setOnClickListener {
                mOnItemClickListener?.onItemClick(dailyForecast)
            }

            itemBinding.date.text =
                DateConverter.getDateTime(dailyForecast.dt.toString())

            // Set weather description(e.g. "Light rain")
            itemBinding.weatherMainDescription.text = dailyForecast.weather[0].main

            // Set Max and Min temperatures
            itemBinding.maxTemp.text =
                dailyForecast.temp.max.roundToInt().toString() + " " + mTempMeasurementUnitSign
            itemBinding.minTemp.text =
                dailyForecast.temp.min.roundToInt().toString() + " " + mTempMeasurementUnitSign

            // Set weather icon
            val iconUri = BuildConfig.ICON_BASE_URL + dailyForecast.weather[0].icon + "@2x.png"
            Picasso.get().load(iconUri)
                .placeholder(R.drawable.weather_placeholder_ic)
                .resize(
                    LayoutHelper.pixels(itemBinding.root.context, 60F),
                    LayoutHelper.pixels(itemBinding.root.context, 60F)
                )
                .error(android.R.drawable.stat_notify_error)
                .into(itemBinding.weatherIcon)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DailyForecastViewHolder {
        val v =
            ItemDayForecastViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return DailyForecastViewHolder(v)
    }

    override fun onBindViewHolder(holder: DailyForecastViewHolder, position: Int) {
        val dailyForecast = mDailyForecastList[position]
        holder.bind(dailyForecast, mTempMeasurementUnitSign, mOnItemClickListener)
    }

    override fun getItemCount(): Int {
        return mDailyForecastList.size
    }

    fun setDailyForecastList(dailyForecastList: List<DailyForecast>) {
        mDailyForecastList = dailyForecastList
        notifyDataSetChanged()
    }

    fun withMeasurementUnit(measurementUnit: String): WeekForecastAdapter {
        mTempMeasurementUnitSign = measurementUnit
        return this
    }

    fun withItemClickListener(itemClickListener: ItemClickListener<DailyForecast>): WeekForecastAdapter {
        mOnItemClickListener = itemClickListener
        return this
    }
}