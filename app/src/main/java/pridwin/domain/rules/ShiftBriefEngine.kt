//Nia Junod & Alina Tarasevich
package pridwin.domain.rules

import pridwin.domain.model.CommuteMode
import pridwin.domain.model.ForecastSlice
import pridwin.domain.model.RiskTag
import pridwin.domain.model.Role
import pridwin.domain.model.ShiftBrief
import pridwin.domain.model.ShiftInstance
import pridwin.domain.model.ShiftPhase
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

object ShiftBriefEngine {


    fun generate(
        role: Role,
        shift: ShiftInstance,
        allSlices: List<ForecastSlice>,
        now: LocalDateTime,
        commuteMode: CommuteMode
    ): ShiftBrief {
        val phase = shift.determinePhase(now)

        val relevant = when (phase) {
            ShiftPhase.PRE_SHIFT -> slicesBetween(
                allSlices,
                shift.startDateTime.minusHours(1),
                shift.startDateTime.plusHours(2)
            )

            ShiftPhase.DURING_SHIFT -> slicesBetween(
                allSlices,
                now,
                now.plusHours(2)
            )

            ShiftPhase.POST_SHIFT -> slicesBetween(
                allSlices,
                shift.endDateTime.minusHours(1),
                shift.endDateTime.plusHours(1)
            )
        }

        val (bullets, tags) = buildBulletsAndTags(role, shift, relevant, phase, now, commuteMode)
        val title = when (phase) {
            ShiftPhase.PRE_SHIFT -> "Pre-shift brief"
            ShiftPhase.DURING_SHIFT -> "During-shift brief"
            ShiftPhase.POST_SHIFT -> "Post-shift brief"
        }

        val summary = when (phase) {
            ShiftPhase.PRE_SHIFT -> {
                val mins = ChronoUnit.MINUTES.between(now, shift.startDateTime).coerceAtLeast(0)
                "Shift starts in ${mins} min (${shift.startTime}–${shift.endTime})"
            }
            ShiftPhase.DURING_SHIFT -> "Next 2 hours outlook"
            ShiftPhase.POST_SHIFT -> "Leaving around ${shift.endTime}"
        }

        return ShiftBrief(
            phase = phase,
            title = title,
            summary = summary,
            bulletPoints = bullets,
            riskTags = tags
        )
    }

    private fun buildBulletsAndTags(
        role: Role,
        shift: ShiftInstance,
        slices: List<ForecastSlice>,
        phase: ShiftPhase,
        now: LocalDateTime,
        commuteMode: CommuteMode
    ): Pair<List<String>, List<RiskTag>> {
        if (slices.isEmpty()) {
            return listOf("Forecast unavailable for this window.") to emptyList()
        }

        val tags = mutableSetOf<RiskTag>()
        val bullets = mutableListOf<String>()

        val minTempC = slices.minOf { it.temperatureC }
        val maxTempC = slices.maxOf { it.temperatureC }
        val maxWind = slices.maxOf { it.windSpeedMps }
        val maxPop = slices.maxOf { it.precipitationProbability }

        val anyRain = slices.any { it.isRain } || maxPop >= 0.5
        val anySnow = slices.any { it.isSnow }


        if (anyRain) {
            tags += RiskTag.RAIN_RISK
            if (role.isOutdoor) bullets += "Rain likely — bring rain jacket / non-slip shoes."
            else bullets += "Rain likely — expect more indoor seating / coat checks."
        }

        if (anySnow) {
            tags += RiskTag.SNOW_OR_ICE
            bullets += "Snow/ice possible — use caution."
        }


        if (maxWind >= 8.0) { // ~18 mph
            tags += RiskTag.WIND_RISK
            if (role.isOutdoor) bullets += "Windy conditions — patio/umbrella stability risk."
            else bullets += "Windy outside — entrances may get gusty; expect coat handling."
        }


        if (maxTempC >= 29.5) { // ~85F
            tags += RiskTag.HEAT_RISK
            if (role.isOutdoor) bullets += "High heat — hydrate early and often."
            else bullets += "Warm conditions — expect higher cold drink demand."
        }


        val endTempC = slices.lastOrNull()?.temperatureC ?: minTempC
        if (phase == ShiftPhase.POST_SHIFT || (phase == ShiftPhase.DURING_SHIFT && ChronoUnit.MINUTES.between(now, shift.endDateTime) <= 120)) {
            if (endTempC <= 4.5) { // ~40F
                tags += RiskTag.COLD_DEPARTURE
                val commuteText = if (commuteMode == CommuteMode.WALK_FROM_PARKING) "Walking from parking" else "Driving"
                bullets += "$commuteText after shift: cold conditions — warm layers recommended."
            }
        }


        val patioUnlikely = anyRain || maxWind >= 8.0 || minTempC <= 10.0 // ~50F
        if (patioUnlikely) {
            tags += RiskTag.PATIO_UNLIKELY
            bullets += "Patio likelihood: unlikely."
        } else if (role.isOutdoor) {
            bullets += "Patio likelihood: possible."
        }


        bullets.add(
            0,
            "Temp window: ${formatC(minTempC)}–${formatC(maxTempC)}."
        )

        return bullets.distinct() to tags.toList()
    }

    private fun slicesBetween(
        all: List<ForecastSlice>,
        startInclusive: LocalDateTime,
        endExclusive: LocalDateTime
    ): List<ForecastSlice> {
        return all.filter { s ->
            !s.dateTime.isBefore(startInclusive) && s.dateTime.isBefore(endExclusive)
        }.sortedBy { it.dateTime }
    }

    private fun formatC(c: Double): String {
        return "${c.toInt()}°C"
    }
}