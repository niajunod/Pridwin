package com.domain.model

data class WeatherInfo(
    val location: String,
    val temperatureC: Double,
    val condition: String,
    val isDay: Boolean
)