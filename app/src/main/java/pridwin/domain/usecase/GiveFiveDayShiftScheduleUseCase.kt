//Nia Junod & Alina Tarasevich
package pridwin.domain.usecase

import pridwin.domain.model.DayShiftStatus
import pridwin.domain.model.ForecastDay
import pridwin.domain.model.Role
import pridwin.domain.rules.ShiftPolicy

class GiveFiveDayShiftScheduleUseCase {

    fun execute(role: Role, days: List<ForecastDay>): List<DayShiftStatus> {
        return days
            .sortedBy { it.date }
            .take(5)
            .map { day -> ShiftPolicy.evaluateDay(role, day) }
    }
}