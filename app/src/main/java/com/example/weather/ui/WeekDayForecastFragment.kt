package com.example.weather.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.example.weather.BuildConfig
import com.example.weather.R
import com.example.weather.data.sharedprefs.PreferenceProvider
import com.example.weather.databinding.FragmentWeekDayForecastBinding
import com.example.weather.utils.DateConverter
import com.example.weather.utils.LayoutHelper
import com.example.weather.viewmodel.WeatherViewModel
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import javax.inject.Inject
import kotlin.math.roundToInt

@AndroidEntryPoint
class WeekDayForecastFragment : Fragment() {

    @set:Inject
    lateinit var mSharedPrefs: PreferenceProvider

    private val mWeatherViewModel: WeatherViewModel by activityViewModels()
    private var _mWeekDayForecastBinding: FragmentWeekDayForecastBinding? = null
    private val mWeekDayForecastBinding get() = _mWeekDayForecastBinding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _mWeekDayForecastBinding =
            FragmentWeekDayForecastBinding.inflate(inflater, container, false)
        return mWeekDayForecastBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        mWeatherViewModel.getSelectedWeekDayForecast().observe(viewLifecycleOwner, {
            with(mWeekDayForecastBinding) {
                date.text = DateConverter.getDateTime(it.dt.toString())
                ("Day " + it.temp.day.roundToInt()
                    .toString() + mSharedPrefs.getTemperatureUnit()).also {
                    tempDay.text = it
                }
                ("Night " + it.temp.night.roundToInt()
                    .toString() + mSharedPrefs.getTemperatureUnit()).also {
                    tempNight.text = it
                }
                ((it.pop * 100).roundToInt()
                    .toString() + "% " + getString(R.string.chance_of_precip)).also {
                    precip.text = it
                }
                description.text = it.weather[0].description.capitalize(Locale.getDefault())

                (getString(R.string.humidity) + " " + it.humidity.toString() + "%").also {
                    humidity.text = it
                }

                val iconUri = BuildConfig.ICON_BASE_URL + it.weather[0].icon + "@2x.png"
                Picasso.get().load(iconUri)
                    .resize(
                        LayoutHelper.pixels(requireActivity(), 80F),
                        LayoutHelper.pixels(requireActivity(), 80F)
                    )
                    .error(android.R.drawable.stat_notify_error)
                    .into(icon)
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _mWeekDayForecastBinding = null
    }
}