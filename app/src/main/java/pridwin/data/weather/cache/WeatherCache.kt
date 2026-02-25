package pridwin.data.weather.cache

import pridwin.domain.model.WeatherInfo


class WeatherCache {
    private var cached: WeatherInfo? = null
    private var lastUpdatedMs: Long = 0L

    fun get(): CachedWeather? {
        val w = cached ?: return null
        return CachedWeather(w, lastUpdatedMs)
    }

    fun set(weather: WeatherInfo, nowMs: Long) {
        cached = weather
        lastUpdatedMs = nowMs
    }

    fun clear() {
        cached = null
        lastUpdatedMs = 0L
    }
}

data class CachedWeather(
    val weather: WeatherInfo,
    val lastUpdatedMs: Long
)