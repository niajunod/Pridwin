package com.data.weather


import com.example.app.domain.model.WeatherInfo
import com.example.app.util.Result

interface WeatherRepository {
    suspend fun getCurrentWeather(lat: Double, lon: Double): Result<WeatherInfo>
}