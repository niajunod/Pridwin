//Nia Junod & Alina Tarasevich
package pridwin.domain.usecase


import pridwin.domain.model.DayShiftStatus
import pridwin.domain.model.ForecastDay
import pridwin.domain.model.Role


class GenerateShiftScheduleUseCase(
    private val giveFiveDay: GiveFiveDayShiftScheduleUseCase = GiveFiveDayShiftScheduleUseCase()
) {
    fun execute(role: Role, forecastDays: List<ForecastDay>): List<DayShiftStatus> {
        return giveFiveDay.execute(role, forecastDays)
    }
}