package com.util


import java.util.Locale
import kotlin.math.roundToInt

object Formatters {

    fun formatTempC(tempC: Double): String {
        return "${tempC.roundToInt()}°C"
    }

    fun titleCase(input: String): String {
        if (input.isBlank()) return input
        return input
            .lowercase(Locale.getDefault())
            .split(" ")
            .joinToString(" ") { word ->
                word.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
            }
    }
}