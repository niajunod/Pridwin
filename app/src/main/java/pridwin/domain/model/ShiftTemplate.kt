//Nia Junod & Alina Tarasevich
package pridwin.domain.model

import java.time.LocalTime

data class ShiftTemplate(
    val role: Role,
    val startTime: LocalTime,
    val endTime: LocalTime
) {
    companion object {
        fun forRole(role: Role): ShiftTemplate {
            return when (role) {
                Role.POOL_SERVER -> ShiftTemplate(role, LocalTime.of(8, 0), LocalTime.of(17, 0))
                Role.POOL_BARTENDER -> ShiftTemplate(role, LocalTime.of(10, 0), LocalTime.of(20, 0))
                Role.POOL_KITCHEN -> ShiftTemplate(role, LocalTime.of(10, 0), LocalTime.of(17, 0))
                Role.BEACH_SERVER -> ShiftTemplate(role, LocalTime.of(11, 0), LocalTime.of(17, 0))

                Role.MAIN_SERVER -> ShiftTemplate(role, LocalTime.of(12, 0), LocalTime.of(23, 0))
                Role.MAIN_BARTENDER -> ShiftTemplate(role, LocalTime.of(12, 0), LocalTime.of(23, 0))
            }
        }
    }
}