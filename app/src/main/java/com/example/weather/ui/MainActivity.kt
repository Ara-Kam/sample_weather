package com.example.weather.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import com.example.weather.databinding.ActivityMainBinding
import com.example.weather.location.*
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var mViewBinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mViewBinding.root)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_CHECK_SETTINGS) {
            val hostFragment: NavHostFragment =
                supportFragmentManager.findFragmentById(com.example.weather.R.id.nav_host_fragment) as NavHostFragment?
                    ?: return
            hostFragment.childFragmentManager.fragments[0].onActivityResult(
                requestCode,
                resultCode,
                data
            )
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }
}