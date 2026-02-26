//Nia Junod & Alina Tarasevich
package pridwin.domain.model

import java.time.LocalDateTime

data class ForecastSlice(
    val dateTime: LocalDateTime,
    val temperatureC: Double,
    val precipitationProbability: Double, // 0.0 - 1.0
    val windSpeedMps: Double,
    val condition: String
) {

    val isRain: Boolean
        get() = condition.contains("rain", ignoreCase = true)

    val isSnow: Boolean
        get() = condition.contains("snow", ignoreCase = true)
}