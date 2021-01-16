package com.example.weather.location

import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat

const val LOCATION_PERMISSION_REQUEST = 0
const val REQUEST_CHECK_SETTINGS = 1

fun AppCompatActivity.checkSelfPermissionCompat(permission: String) =
    ActivityCompat.checkSelfPermission(this, permission)

fun AppCompatActivity.shouldShowRequestPermissionRationaleCompat(permission: String) =
    ActivityCompat.shouldShowRequestPermissionRationale(this, permission)

fun AppCompatActivity.requestPermissionsCompat(
    permissionsArray: Array<String>,
    requestCode: Int
) {
    ActivityCompat.requestPermissions(this, permissionsArray, requestCode)
}