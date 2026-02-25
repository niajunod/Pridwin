package pridwin.data.weather.api

import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {

    /**
     * Current weather endpoint
     * https://api.openweathermap.org/data/2.5/weather
     */
    @GET("weather")
    suspend fun getCurrentWeather(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("units") units: String = "imperial",
        @Query("appid") apiKey: String
    ): WeatherDto


    /**
     * 5 Day / 3 Hour Forecast endpoint
     * https://api.openweathermap.org/data/2.5/forecast
     */
    @GET("forecast")
    suspend fun getFiveDayForecast(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("units") units: String = "imperial",
        @Query("appid") apiKey: String
    ): ForecastDto
}