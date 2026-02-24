// data/weather/WeatherRepositoryImpl.kt
package pridwin.data.weather

import pridwin.data.weather.api.WeatherApi
import pridwin.data.weather.cache.WeatherCache
import pridwin.domain.model.WeatherInfo
import pridwin.util.Constants
import pridwin.util.TimeUtils

class WeatherRepositoryImpl(
    private val api: WeatherApi,
    private val cache: WeatherCache
) {

    /**
     * Returns kotlin.Result<WeatherInfo>.
     *
     * If you have a domain interface `WeatherRepository`, change the class declaration to:
     * `class WeatherRepositoryImpl(...) : WeatherRepository`
     * and ensure `WeatherRepository`'s signature matches this method.
     */
    suspend fun getCurrentWeather(lat: Double, lon: Double): Result<WeatherInfo> {
        val nowMs = System.currentTimeMillis()

        // Try cached value first
        val cached = cache.get()
        if (cached != null && TimeUtils.isFresh(nowMs, cached.lastUpdatedMs, Constants.WEATHER_CACHE_TTL_MS)) {
            return Result.success(cached.weather)
        }

        return try {
            val dto = api.getCurrentWeather(
                lat = lat,
                lon = lon,
                apiKey = Constants.OPEN_WEATHER_API_KEY,
                units = Constants.DEFAULT_UNITS
            )

            val mapped = WeatherMapper.fromDto(dto)
            cache.set(mapped, nowMs) // keep your existing cache API call
            Result.success(mapped)
        } catch (t: Throwable) {
            Result.failure(t)
        }
    }
}