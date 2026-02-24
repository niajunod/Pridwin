// data/weather/WeatherRepositoryImpl.kt
package com.example.pridwin.data.weather

import com.data.weather.cache.WeatherCache
import com.data.weather.api.WeatherApi
import com.example.pridwin.domain.model.WeatherInfo
import com.util.Constants
import com.example.pridwin.util.TimeUtils

class WeatherRepositoryImpl(
    private val api: WeatherApi,
    private val cache: WeatherCache
) : WeatherRepository {

    override suspend fun getCurrentWeather(lat: Double, lon: Double): Result<WeatherInfo> {
        val nowMs = System.currentTimeMillis()

        val cached = cache.get()
        if (cached != null && TimeUtils.isFresh(nowMs, cached.lastUpdatedMs, Constants.WEATHER_CACHE_TTL_MS)) {
            return Result.Success(cached.weather)
        }

        return try {
            val dto = api.getCurrentWeather(
                lat = lat,
                lon = lon,
                apiKey = Constants.OPEN_WEATHER_API_KEY,
                units = Constants.DEFAULT_UNITS
            )
            val mapped = WeatherMapper.fromDto(dto)
            cache.set(mapped, nowMs)
            Result.Success(mapped)
        } catch (t: Throwable) {
            Result.Error(message = t.message ?: "Weather request failed", throwable = t)
        }
    }
}