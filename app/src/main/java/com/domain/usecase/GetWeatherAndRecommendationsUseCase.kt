package com.domain.usecase


import com.example.app.domain.model.*
import kotlinx.coroutines.delay
import kotlin.random.Random

data class WeatherAndRecommendations(
    val weather: WeatherInfo,
    val context: ContextState,
    val recommendations: List<Recommendation>
)

class GetWeatherAndRecommendationsUseCase(
    private val inferContextUseCase: InferContextUseCase = InferContextUseCase()
) {

    suspend operator fun invoke(): WeatherAndRecommendations {
        delay(1000)

        val mockWeather = WeatherInfo(
            location = "Worcester, MA",
            temperatureC = Random.nextDouble(0.0, 20.0),
            condition = listOf("Sunny", "Rain", "Cloudy").random(),
            isDay = Random.nextBoolean()
        )

        val (context, recommendations) =
            inferContextUseCase(mockWeather)

        return WeatherAndRecommendations(
            weather = mockWeather,
            context = context,
            recommendations = recommendations
        )
    }
}