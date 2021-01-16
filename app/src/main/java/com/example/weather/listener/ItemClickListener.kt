package com.example.weather.listener

interface ItemClickListener<T> {
    fun onItemClick(target: T)
}