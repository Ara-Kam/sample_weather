package com.example.weather.utils.timber

import timber.log.Timber

class ReleaseTree : Timber.Tree() {
    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        TODO("Not yet implemented")
    }
}