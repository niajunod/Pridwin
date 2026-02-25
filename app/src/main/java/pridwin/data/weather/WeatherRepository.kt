package pridwin.data.weather

import pridwin.domain.model.ForecastDay
import pridwin.domain.model.WeatherInfo

interface WeatherRepository {
    suspend fun getCurrentWeather(lat: Double, lon: Double): WeatherInfo
    suspend fun getFiveDayForecast(lat: Double, lon: Double): List<ForecastDay>
}
