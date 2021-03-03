package com.example.weather.ui

import android.Manifest
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.weather.BuildConfig
import com.example.weather.data.entity.DailyForecast
import com.example.weather.data.sharedprefs.PreferenceProvider
import com.example.weather.data.util.RemoteDataWrapper
import com.example.weather.databinding.FragmentWeekForecastBinding
import com.example.weather.listener.ItemClickListener
import com.example.weather.location.*
import com.example.weather.ui.adapter.WeekForecastAdapter
import com.example.weather.utils.VerticalSpaceItemDecoration
import com.example.weather.viewmodel.WeatherViewModel
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.example.weather.R
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject


@AndroidEntryPoint
class WeekForecastFragment : Fragment() {

    @set:Inject
    lateinit var mWeekForecastAdapter: WeekForecastAdapter

    @set:Inject
    lateinit var mSharedPrefs: PreferenceProvider

    private val mWeatherViewModel: WeatherViewModel by activityViewModels()
    private var _mWeekForecastBinding: FragmentWeekForecastBinding? = null
    private val mWeekForecastBinding get() = _mWeekForecastBinding!!

    private var mLocationRequest: LocationRequest = LocationRequest.create().apply {
        interval = 10000
        fastestInterval = 5000
        priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
        numUpdates = 1
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _mWeekForecastBinding = FragmentWeekForecastBinding.inflate(inflater, container, false)
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

        mWeatherViewModel.weekForecast?.observe(viewLifecycleOwner, Observer {
            when (it.status) {
                RemoteDataWrapper.Status.SUCCESS -> {
                    if (it.data == null) return@Observer
                    val dailyForecasts = it.data.daily
                    if (!dailyForecasts.isNullOrEmpty()) {
                        mWeekForecastBinding.apply {
                            loadingView.visibility = View.GONE
                            errorLayout.visibility = View.GONE
                        }
                        mWeekForecastAdapter
                            .withMeasurementUnit(mSharedPrefs.getTemperatureUnit())
                            .setDailyForecastList(dailyForecasts)
                    }
                }
                RemoteDataWrapper.Status.ERROR -> {
                    mWeekForecastBinding.apply {
                        loadingView.visibility = View.GONE
                        errorLayout.visibility = View.VISIBLE
                        (it.message + getString(R.string.check_connection)).also {
                            errorText.text = it
                        }
                    }
                }

                RemoteDataWrapper.Status.LOADING -> {
                    mWeekForecastBinding.apply {
                        errorLayout.visibility = View.GONE
                        loadingView.visibility = View.VISIBLE
                    }
                }
            }
        })

        mWeekForecastBinding.retry.setOnClickListener {
            mWeatherViewModel.tryFetchingData()
        }

        if (!mWeatherViewModel.isLocationFetched) {
            getLastKnownLocation()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _mWeekForecastBinding = null
    }

    private fun getLocation() {
        mWeatherViewModel.getLocationData().observe(viewLifecycleOwner, {
            mWeatherViewModel.setRequiredParams(
                it.latitude.toFloat(),
                it.longitude.toFloat(),
                BuildConfig.API_ID
            )
        })
    }

    private fun getLastKnownLocation() {
        // Check if the Location permission has been granted.
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            // Permission is already available.
            checkLocationSettings()
        } else {
            // Permission is missing and must be requested.
            requestLocationPermission()
        }
    }

    private fun checkLocationSettings() {
        val builder = LocationSettingsRequest.Builder().addLocationRequest(mLocationRequest)
        val result = LocationServices.getSettingsClient(requireActivity())
            .checkLocationSettings(builder.build())

        result.addOnCompleteListener { task ->
            Timber.d("onComplete() called with: task =  ${task.isComplete} ")
            getLocation()
        }
        result.addOnFailureListener {
            Timber.d("onFailure() called with: [$it]")
            if (it is ResolvableApiException) {
                try {
                    // Handle result in onActivityResult()
                    it.startResolutionForResult(
                        requireActivity(),
                        REQUEST_CHECK_SETTINGS
                    )
                } catch (sendEx: IntentSender.SendIntentException) {
                    // Ignore the error.
                    Timber.e("IntentSender.SendIntentException: [$sendEx]")
                }
            }
        }
    }

    private fun requestLocationPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                requireActivity(), Manifest.permission.ACCESS_COARSE_LOCATION
            )
        ) {
            // TODO: Provide an additional rationale to the user if the permission was not granted...
            requestPermissions(
                arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
                LOCATION_PERMISSION_REQUEST
            )
        } else {
            requestPermissions(
                arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
                LOCATION_PERMISSION_REQUEST
            )
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_CHECK_SETTINGS) {
            when (resultCode) {
                // All required changes were successfully made.
                AppCompatActivity.RESULT_OK -> getLocation()
                // User was asked to change settings, but chose not to do.
                // TODO: Provide an additional rationale ...
                AppCompatActivity.RESULT_CANCELED -> Timber.d("RESULT CANCELED")
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == LOCATION_PERMISSION_REQUEST) {
            if (grantResults.size == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                checkLocationSettings()
            } else {
                Timber.d("Permission request was denied")
                // TODO: Provide an additional rationale ...
            }
        }
    }
}