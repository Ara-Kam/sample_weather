package com.example.weather.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.weather.BuildConfig
import com.example.weather.R
import com.example.weather.data.entity.CityForecast
import com.example.weather.databinding.ItemCityForecastBinding
import com.example.weather.listener.ItemClickListener
import com.example.weather.utils.DateConverter
import com.example.weather.utils.LayoutHelper
import com.squareup.picasso.Picasso
import javax.inject.Inject
import kotlin.math.roundToInt

class CitiesForecastAdapter @Inject constructor() :
    RecyclerView.Adapter<CitiesForecastAdapter.CityForecastViewHolder>() {

    var mOnItemClickListener: ItemClickListener<CityForecast>? = null
    private var mCityForecastList = emptyList<CityForecast>()
    private var mTempMeasurementUnitSign = ""

    class CityForecastViewHolder(private val itemBinding: ItemCityForecastBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(
            cityForecast: CityForecast,
            mTempMeasurementUnitSign: String,
            mOnItemClickListener: ItemClickListener<CityForecast>?
        ) {
            itemBinding.root.setOnClickListener {
                mOnItemClickListener?.onItemClick(cityForecast)
            }

            itemBinding.date.text =
                DateConverter.getDateTime(cityForecast.dt)

            itemBinding.cityName.text = cityForecast.name

            // Set Max and Min temperatures
            itemBinding.maxTemp.text =
                cityForecast.main.temp_max.roundToInt().toString() + " " + mTempMeasurementUnitSign
            itemBinding.minTemp.text =
                cityForecast.main.temp_min.roundToInt().toString() + " " + mTempMeasurementUnitSign

            // Set weather icon
            val iconUri = BuildConfig.ICON_BASE_URL + cityForecast.weather[0].icon + "@2x.png"
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

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CityForecastViewHolder {
        return CityForecastViewHolder(
            ItemCityForecastBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(
        holder: CityForecastViewHolder,
        position: Int
    ) {
        val cityForecast = mCityForecastList[position]
        holder.bind(cityForecast, mTempMeasurementUnitSign, mOnItemClickListener)
    }

    override fun getItemCount(): Int {
        return mCityForecastList.size
    }

    fun setCitiesForecastList(cityForecastList: List<CityForecast>) {
        mCityForecastList = cityForecastList
        notifyDataSetChanged()
    }

    fun withMeasurementUnit(measurementUnit: String): CitiesForecastAdapter {
        mTempMeasurementUnitSign = measurementUnit
        return this
    }

    fun withItemClickListener(itemClickListener: ItemClickListener<CityForecast>): CitiesForecastAdapter {
        mOnItemClickListener = itemClickListener
        return this
    }
}