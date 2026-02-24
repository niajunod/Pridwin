package pridwin.domain.model

import java.time.LocalDate

data class ForecastDay(
    val date: LocalDate,
    val slices: List<ForecastSlice>
) {

    val highTempC: Double?
        get() = slices.maxOfOrNull { it.temperatureC }

    val lowTempC: Double?
        get() = slices.minOfOrNull { it.temperatureC }

    val isRainDay: Boolean
        get() = slices.any { it.isRain }

    val isSnowDay: Boolean
        get() = slices.any { it.isSnow }
}