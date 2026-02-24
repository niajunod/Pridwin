package pridwin.domain.model

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

data class ShiftInstance(
    val role: Role,
    val date: LocalDate,
    val startTime: LocalTime,
    val endTime: LocalTime
) {

    val startDateTime: LocalDateTime
        get() = LocalDateTime.of(date, startTime)

    val endDateTime: LocalDateTime
        get() = LocalDateTime.of(date, endTime)

    fun determinePhase(now: LocalDateTime): ShiftPhase {
        return when {
            now.isBefore(startDateTime) -> ShiftPhase.PRE_SHIFT
            now.isAfter(endDateTime) -> ShiftPhase.POST_SHIFT
            else -> ShiftPhase.DURING_SHIFT
        }
    }
}