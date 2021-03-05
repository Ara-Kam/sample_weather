package com.example.weather.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.NavHostFragment
import com.example.weather.R
import com.example.weather.databinding.ActivityMainBinding
import com.example.weather.location.*
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var mViewBinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mViewBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)


        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { activityResult ->
            mOnActivityResult(
                REQUEST_CHECK_SETTINGS,
                activityResult.resultCode,
                activityResult.data
            )
        }
    }

    private fun mOnActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_CHECK_SETTINGS) {
            val hostFragment: NavHostFragment =
                supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment?
                    ?: return
            hostFragment.childFragmentManager.fragments[0].onActivityResult(
                requestCode,
                resultCode,
                data
            )
        }
    }
}