package pridwin.domain.rules

import pridwin.domain.model.DayShiftStatus
import pridwin.domain.model.ForecastDay
import pridwin.domain.model.Role
import pridwin.domain.model.ShiftStatus

object ShiftPolicy {

    fun evaluateDay(role: Role, day: ForecastDay): DayShiftStatus {
        val weatherRisk = day.isRainDay || day.isSnowDay

        return if (role.isOutdoor && weatherRisk) {
            val reason = if (day.isSnowDay) "Snow/ice forecasted — outdoor position" else "Rain forecasted — outdoor position"
            DayShiftStatus(date = day.date, status = ShiftStatus.OFF, reason = reason)
        } else {
            val reason = if (!role.isOutdoor) "Weather does not affect this role" else "No rain forecasted"
            DayShiftStatus(date = day.date, status = ShiftStatus.WORKING, reason = reason)
        }
    }
}