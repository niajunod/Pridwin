//Nia Junod & Alina Tarasevich
package pridwin.data.weather

import pridwin.data.weather.api.WeatherApi
import pridwin.domain.model.ForecastDay
import pridwin.domain.model.WeatherInfo
import java.time.ZoneId

class WeatherRepositoryImpl(
    private val api: WeatherApi,
    private val apiKey: String,
    private val zoneId: ZoneId = ZoneId.systemDefault()
) : WeatherRepository {

    override suspend fun getCurrentWeather(lat: Double, lon: Double): WeatherInfo {
        val dto = api.getCurrentWeather(
            lat = lat,
            lon = lon,
            units = "imperial",
            apiKey = apiKey
        )
        return WeatherMapper.fromDto(dto)
    }

    override suspend fun getFiveDayForecast(lat: Double, lon: Double): List<ForecastDay> {
        val dto = api.getFiveDayForecast(
            lat = lat,
            lon = lon,
            units = "imperial",
            apiKey = apiKey
        )
        return ForecastMapper.fromDto(dto, zoneId)
    }
}