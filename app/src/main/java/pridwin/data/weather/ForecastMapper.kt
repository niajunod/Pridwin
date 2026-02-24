package pridwin.data.weather

import pridwin.data.weather.api.ForecastDto
import pridwin.domain.model.ForecastDay
import pridwin.domain.model.ForecastSlice
import pridwin.util.Formatters
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId

object ForecastMapper {

    fun fromDto(dto: ForecastDto, zoneId: ZoneId = ZoneId.systemDefault()): List<ForecastDay> {
        val items = dto.list.orEmpty()

        val slices: List<ForecastSlice> = items.mapNotNull { item ->
            val dt = item.dt ?: return@mapNotNull null
            val tempC = item.main?.temp ?: return@mapNotNull null

            val windMps = item.wind?.speed ?: 0.0
            val pop = item.pop ?: 0.0

            val conditionRaw =
                item.weather?.firstOrNull()?.description
                    ?: item.weather?.firstOrNull()?.main
                    ?: "Unknown"

            val condition = Formatters.titleCase(conditionRaw)

            val dateTime = LocalDateTime.ofInstant(Instant.ofEpochSecond(dt), zoneId)

            ForecastSlice(
                dateTime = dateTime,
                temperatureC = tempC,
                precipitationProbability = pop, // 0.0 - 1.0
                windSpeedMps = windMps,
                condition = condition
            )
        }

        val byDay: Map<LocalDate, List<ForecastSlice>> =
            slices.groupBy { it.dateTime.toLocalDate() }

        return byDay.entries
            .sortedBy { it.key }
            .map { (date, daySlices) ->
                ForecastDay(
                    date = date,
                    slices = daySlices.sortedBy { it.dateTime }
                )
            }
    }
}