package pridwin.domain.usecase


import pridwin.domain.model.DayShiftStatus
import pridwin.domain.model.ForecastDay
import pridwin.domain.model.Role

/**
 * Thin wrapper over GiveFiveDayShiftScheduleUseCase to match your requested file list.
 * Keep both for clarity; you can delete this later if you want.
 */
class GenerateShiftScheduleUseCase(
    private val giveFiveDay: GiveFiveDayShiftScheduleUseCase = GiveFiveDayShiftScheduleUseCase()
) {
    fun execute(role: Role, forecastDays: List<ForecastDay>): List<DayShiftStatus> {
        return giveFiveDay.execute(role, forecastDays)
    }
}