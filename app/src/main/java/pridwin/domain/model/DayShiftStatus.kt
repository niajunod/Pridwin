//Nia Junod & Alina Tarasevich
package pridwin.domain.model

import java.time.LocalDate

data class DayShiftStatus(
    val date: LocalDate,
    val status: ShiftStatus,
    val reason: String
)