package com.example.weather.ui

import android.Manifest
import android.annotation.SuppressLint
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
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.weather.R
import com.example.weather.data.entity.CityForecast
import com.example.weather.data.sharedprefs.PreferenceProvider
import com.example.weather.data.util.RemoteDataWrapper
import com.example.weather.databinding.FragmentCitiesForecastBinding
import com.example.weather.listener.ItemClickListener
import com.example.weather.location.LOCATION_PERMISSION_REQUEST
import com.example.weather.location.MyLocationModel
import com.example.weather.location.REQUEST_CHECK_SETTINGS
import com.example.weather.ui.adapter.CitiesForecastAdapter
import com.example.weather.utils.VerticalSpaceItemDecoration
import com.example.weather.viewmodel.WeatherViewModel
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject
import kotlin.math.roundToInt

@AndroidEntryPoint
class CitiesForecastFragment : Fragment() {

    @set:Inject
    lateinit var mCitiesForecastAdapter: CitiesForecastAdapter

    @set:Inject
    lateinit var mSharedPrefs: PreferenceProvider

    private val mWeatherViewModel: WeatherViewModel by activityViewModels()
    private lateinit var mCitiesForecastBinding: FragmentCitiesForecastBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mCitiesForecastBinding = FragmentCitiesForecastBinding.inflate(inflater, container, false)
        return mCitiesForecastBinding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        mCitiesForecastBinding.lifecycleOwner = viewLifecycleOwner
        mCitiesForecastBinding.viewModel = mWeatherViewModel

        mCitiesForecastBinding.citiesForecastList.apply {
            addItemDecoration(
                VerticalSpaceItemDecoration(context, 8f, 0f, 0f)
            )
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            adapter = mCitiesForecastAdapter.withItemClickListener(object :
                ItemClickListener<CityForecast> {
                override fun onItemClick(target: CityForecast) {
                    // Show 7 day forecast for selected city
                    findNavController().navigate(
                        CitiesForecastFragmentDirections.actionCitiesForecastFragmentToWeekForecastFragment(
                            MyLocationModel(
                                target.coord.lon,
                                target.coord.lat
                            )
                        )
                    )
                }
            })
        }

        mWeatherViewModel.currentForecastResult.observe(viewLifecycleOwner, Observer {
            when (it.status) {
                RemoteDataWrapper.Status.SUCCESS -> {
                    if (it.data == null) return@Observer
                    val currentForecast = it.data
                    mCitiesForecastBinding.currTemp.text =
                        "${currentForecast.current.temp.roundToInt()} ${mSharedPrefs.getTemperatureUnit()}"
                }
                RemoteDataWrapper.Status.ERROR -> {
                    (it.message + getString(R.string.check_connection)).also {
                        mCitiesForecastBinding.errorLayout.errorText.text = it
                    }
                }
                RemoteDataWrapper.Status.LOADING -> {
                }
            }
        })

        mWeatherViewModel.citiesForecastResult.observe(viewLifecycleOwner, Observer {
            when (it.status) {
                RemoteDataWrapper.Status.SUCCESS -> {
                    if (it.data == null) return@Observer
                    val citiesForecast = it.data
                    if (!citiesForecast.isNullOrEmpty()) {
                        mCitiesForecastAdapter
                            .withMeasurementUnit(mSharedPrefs.getTemperatureUnit())
                            .setCitiesForecastList(citiesForecast)
                    }
                }
                RemoteDataWrapper.Status.ERROR -> {
                    mCitiesForecastBinding.errorLayout.root.visibility = View.VISIBLE
                    (it.message + getString(R.string.check_connection)).also {
                        mCitiesForecastBinding.errorLayout.errorText.text = it
                    }
                }
                RemoteDataWrapper.Status.LOADING -> {
                }
            }
        })

        mCitiesForecastBinding.errorLayout.retry.setOnClickListener {
            // TODO :: Separate error handling..
            mWeatherViewModel.getCurrentForecast()
            mWeatherViewModel.getCitiesForecast()
        }

        mCitiesForecastBinding.weekForecast.setOnClickListener {
            // Show 7 day forecast for current city
            findNavController().navigate(
                CitiesForecastFragmentDirections.actionCitiesForecastFragmentToWeekForecastFragment(
                    mSharedPrefs.getLastKnownLocation()
                )
            )
        }

        if (!mWeatherViewModel.isLocationFetched) {
            getLastKnownLocation()
        }
    }

    private fun getLocation() {
        mWeatherViewModel.getCitiesForecast()
        mWeatherViewModel.getLocationData().observe(viewLifecycleOwner, {
            mSharedPrefs.setLastKnownLocation(MyLocationModel(it.longitude, it.latitude))
            mWeatherViewModel.fetchCurrentForecast()
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
                mCitiesForecastBinding.currentLocationForecast.visibility = View.GONE
                Timber.d("Permission request was denied")
                // TODO: Provide an additional rationale ...
            }
        }
    }

    private var mLocationRequest: LocationRequest = LocationRequest.create().apply {
        interval = 10000
        fastestInterval = 5000
        priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
        numUpdates = 1
    }
}