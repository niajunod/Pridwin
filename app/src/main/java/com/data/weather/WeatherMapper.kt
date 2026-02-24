package com.data.weather


import com.example.app.data.weather.api.WeatherDto
import com.example.app.domain.model.WeatherInfo
import com.example.app.util.Formatters
import com.example.app.util.TimeUtils

object WeatherMapper {

    fun fromDto(dto: WeatherDto): WeatherInfo {
        val locationName = dto.name?.takeIf { it.isNotBlank() } ?: "Unknown location"

        val tempC = dto.main?.temp ?: Double.NaN

        val conditionRaw =
            dto.weather?.firstOrNull()?.description
                ?: dto.weather?.firstOrNull()?.main
                ?: "Unknown"

        val condition = Formatters.titleCase(conditionRaw)

        val dt = dto.dt ?: 0L
        val sunrise = dto.sys?.sunrise
        val sunset = dto.sys?.sunset

        val isDay = TimeUtils.isDay(dtSec = dt, sunriseSec = sunrise, sunsetSec = sunset)

        return WeatherInfo(
            location = locationName,
            temperatureC = tempC,
            condition = condition,
            isDay = isDay
        )
    }
}