//Nia Junod & Alina Tarasevich
package pridwin.domain.usecase

import pridwin.domain.model.Role
import pridwin.domain.model.ShiftInstance
import pridwin.domain.model.ShiftTemplate
import java.time.LocalDateTime

class GetNextShiftInstanceUseCase {


    fun execute(role: Role, now: LocalDateTime): ShiftInstance {
        val template = ShiftTemplate.forRole(role)
        val today = now.toLocalDate()

        val todayShiftStart = LocalDateTime.of(today, template.startTime)
        val date = if (now.isBefore(todayShiftStart)) today else today.plusDays(1)

        return ShiftInstance(
            role = role,
            date = date,
            startTime = template.startTime,
            endTime = template.endTime
        )
    }
}