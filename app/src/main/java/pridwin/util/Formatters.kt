//Nia Junod & Alina Tarasevich
package pridwin.util

import java.util.Locale
import kotlin.math.roundToInt

object Formatters {
    fun formatTempC(tempC: Double): String = "${tempC.roundToInt()}°C"

    fun titleCase(input: String): String {
        if (input.isBlank()) return input
        return input.lowercase(Locale.getDefault())
            .split(" ")
            .joinToString(" ") { w ->
                w.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
            }
    }
}