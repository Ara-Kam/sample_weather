package com.example.weather.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.weather.data.entity.DailyForecast
import com.example.weather.data.sharedprefs.PreferenceProvider
import com.example.weather.data.util.RemoteDataWrapper
import com.example.weather.databinding.FragmentWeekForecastBinding
import com.example.weather.listener.ItemClickListener
import com.example.weather.location.*
import com.example.weather.ui.adapter.WeekForecastAdapter
import com.example.weather.utils.VerticalSpaceItemDecoration
import com.example.weather.viewmodel.WeatherViewModel
import com.google.android.gms.location.*
import com.example.weather.R
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class WeekForecastFragment : Fragment() {

    @set:Inject
    lateinit var mWeekForecastAdapter: WeekForecastAdapter

    @set:Inject
    lateinit var mSharedPrefs: PreferenceProvider

    private val mWeatherViewModel: WeatherViewModel by activityViewModels()
    private lateinit var mWeekForecastBinding: FragmentWeekForecastBinding
    private lateinit var mLocation: MyLocationModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mWeekForecastBinding = FragmentWeekForecastBinding.inflate(inflater, container, false)
        mWeekForecastBinding.viewModel = mWeatherViewModel
        mWeekForecastBinding.lifecycleOwner = viewLifecycleOwner

        mLocation =
            arguments?.let { WeekForecastFragmentArgs.fromBundle(it).location } ?: MyLocationModel(
                40.177568, 44.512587
            )
        return mWeekForecastBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        mWeekForecastBinding.daysList.apply {
            addItemDecoration(
                VerticalSpaceItemDecoration(context, 8f, 0f, 0f)
            )
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            adapter = mWeekForecastAdapter.withItemClickListener(object :
                ItemClickListener<DailyForecast> {
                override fun onItemClick(target: DailyForecast) {
                    mWeatherViewModel.setSelectedWeekDayForecast(target)
                    // Navigate to detail screen
                    findNavController().navigate(
                        WeekForecastFragmentDirections.actionWeekForecastFragmentToWeekDayForecastFragment()
                    )
                }
            })
        }

        mWeatherViewModel.weekForecastResult
            .observe(viewLifecycleOwner, Observer {
                if (it == null) return@Observer
                when (it.status) {
                    RemoteDataWrapper.Status.SUCCESS -> {
                        if (it.data == null) return@Observer
                        val dailyForecasts = it.data.daily
                        if (!dailyForecasts.isNullOrEmpty()) {
                            mWeekForecastAdapter
                                .withMeasurementUnit(mSharedPrefs.getTemperatureUnit())
                                .setDailyForecastList(dailyForecasts)
                        }
                    }
                    RemoteDataWrapper.Status.ERROR -> {
                        mWeekForecastBinding.apply {
                            (it.message + getString(R.string.check_connection)).also {
                                mWeekForecastBinding.errorLayout.errorText.text = it
                            }
                        }
                    }

                    RemoteDataWrapper.Status.LOADING -> {
                    }
                }
            })

        mWeekForecastBinding.errorLayout.retry.setOnClickListener {
            mWeatherViewModel.getWeekForecast(
                mLocation.latitude,
                mLocation.longitude
            )
        }

        mWeatherViewModel.getWeekForecast(mLocation.latitude, mLocation.longitude)
    }
}