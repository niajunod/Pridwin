package pridwin.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import pridwin.domain.model.ForecastDay
import pridwin.viewmodel.ForecastUiState
import pridwin.viewmodel.WeatherViewModel
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForecastScreen(
    role: String,
    onBack: () -> Unit,
    weatherVm: WeatherViewModel,
    modifier: Modifier = Modifier
) {
    val roleTitle = when (role) {
        "pool_server" -> "Pool Server"
        "beach_server" -> "Beach Server"
        "main_server" -> "Main Server"
        "main_bartender" -> "Main Bartender"
        else -> role.replaceFirstChar { it.uppercase() }
    }

    val forecast by weatherVm.forecastUiState.collectAsState()

    LaunchedEffect(role) {
        weatherVm.refreshForecast()
    }

    val dayFmt = remember { DateTimeFormatter.ofPattern("EEE, MMM d", Locale.US) }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text("Forecast: $roleTitle") },
                navigationIcon = { TextButton(onClick = onBack) { Text("Back") } }
            )
        }
    ) { inner ->
        Column(
            modifier = Modifier
                .padding(inner)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("5-Day Forecast", style = MaterialTheme.typography.headlineSmall)
            Text("Role: $roleTitle", style = MaterialTheme.typography.titleMedium)

            HorizontalDivider()

            when (val state = forecast) {
                is ForecastUiState.Idle -> {
                    Text("No forecast yet.")
                }

                is ForecastUiState.Loading -> {
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        CircularProgressIndicator()
                        Text("Loading forecast...")
                    }
                }

                is ForecastUiState.Error -> {
                    Text(state.message)
                    Spacer(Modifier.height(8.dp))
                    Button(onClick = { weatherVm.refreshForecast() }) {
                        Text("Retry")
                    }
                }

                is ForecastUiState.Ready -> {
                    val days = state.days
                    if (days.isEmpty()) {
                        Text("No forecast data returned.")
                    } else {
                        days.take(5).forEachIndexed { idx, day ->
                            val summary = summarizeDay(day)
                            val status = shiftStatus(role, summary)

                            ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    val dateLabel = try {
                                        day.date.format(dayFmt)
                                    } catch (_: Throwable) {
                                        day.date.toString()
                                    }

                                    Text(
                                        text = "Day ${idx + 1} — $status",
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                    Text(dateLabel, style = MaterialTheme.typography.bodyMedium)

                                    Text(
                                        text = "High: ${summary.highF} °F   Low: ${summary.lowF} °F"
                                    )
                                    Text("Condition: ${summary.condition}")

                                    Text(
                                        text = "Rain chance: ${summary.popPct}%   Wind: ${summary.windMph} mph",
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                            }
                        }

                        if (days.size < 5) {
                            Spacer(Modifier.height(6.dp))
                            Text(
                                "Only ${days.size} day(s) are being produced by your ForecastMapper right now.",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }
        }
    }
}

private data class DaySummary(
    val highF: Int,
    val lowF: Int,
    val condition: String,
    val popPct: Int,
    val windMph: Int
)

private fun summarizeDay(day: ForecastDay): DaySummary {
    // Based on your debug output:
    // slice.temperatureC is actually in °F because your API call uses units="imperial"
    val temps = day.slices.mapNotNull { it.temperatureC }
    val pops = day.slices.mapNotNull { it.precipitationProbability }
    val winds = day.slices.mapNotNull { it.windSpeedMps }
    val conditions = day.slices.mapNotNull { it.condition }

    val high = temps.maxOrNull() ?: Double.NaN
    val low = temps.minOrNull() ?: Double.NaN

    val popMax = pops.maxOrNull() ?: 0.0
    val windAvgMps = if (winds.isNotEmpty()) winds.average() else 0.0

    val condition = conditions
        .groupingBy { it }
        .eachCount()
        .maxByOrNull { it.value }
        ?.key ?: "—"

    val windMph = (windAvgMps * 2.23694).roundToInt()

    return DaySummary(
        highF = if (high.isNaN()) 0 else high.roundToInt(),
        lowF = if (low.isNaN()) 0 else low.roundToInt(),
        condition = condition,
        popPct = (popMax * 100).roundToInt().coerceIn(0, 100),
        windMph = windMph.coerceAtLeast(0)
    )
}

private fun shiftStatus(role: String, s: DaySummary): String {
    val cond = s.condition.lowercase()
    val wet = s.popPct >= 50 ||
            cond.contains("rain") ||
            cond.contains("storm") ||
            cond.contains("thunder") ||
            cond.contains("snow")

    return when (role) {
        "pool_server" -> when {
            wet -> "OFF"
            s.highF >= 75 -> "ON"
            s.highF >= 68 -> "TENTATIVE"
            else -> "OFF"
        }

        "beach_server" -> when {
            wet -> "OFF"
            s.highF >= 72 -> "ON"
            s.highF >= 65 -> "TENTATIVE"
            else -> "OFF"
        }

        "main_server" -> when {
            cond.contains("snow") && s.lowF <= 28 -> "TENTATIVE"
            else -> "ON"
        }

        "main_bartender" -> when {
            cond.contains("snow") && s.lowF <= 25 -> "TENTATIVE"
            else -> "ON"
        }

        else -> "TENTATIVE"
    }
}