package pridwin.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
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
    val roleKey = remember(role) { normalizeRoleKey(role) }
    val roleTitle = remember(roleKey) { prettyRoleFromKey(roleKey) }

    val forecast by weatherVm.forecastUiState.collectAsState()

    LaunchedEffect(roleKey) {
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
        when (val state = forecast) {
            is ForecastUiState.Idle -> CenterText(inner, "No forecast yet.")
            is ForecastUiState.Loading -> CenterLoading(inner)
            is ForecastUiState.Error -> ErrorBlock(inner, state.message) { weatherVm.refreshForecast() }

            is ForecastUiState.Ready -> {
                val days = state.days.take(5)

                LazyColumn(
                    modifier = Modifier
                        .padding(inner)
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        Text("5-Day Forecast", style = MaterialTheme.typography.headlineSmall)
                        Text("Role: $roleTitle", style = MaterialTheme.typography.titleSmall)
                        Spacer(Modifier.height(6.dp))
                        HorizontalDivider()
                        Spacer(Modifier.height(4.dp))
                    }

                    itemsIndexed(days) { idx, day ->
                        val s = summarizeDay(day)
                        val status = shiftStatus(roleKey, s)

                        ElevatedCard(
                            modifier = Modifier.fillMaxWidth(),
                            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                val dateLabel = try {
                                    day.date.format(dayFmt)
                                } catch (_: Throwable) {
                                    day.date.toString()
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Day ${idx + 1} — $status",
                                        style = MaterialTheme.typography.titleLarge
                                    )
                                    Text(
                                        text = dateLabel,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }

                                Text(
                                    text = "High ${s.highF}°F   Low ${s.lowF}°F",
                                    style = MaterialTheme.typography.bodyLarge
                                )

                                Text(
                                    text = s.condition,
                                    style = MaterialTheme.typography.bodyLarge
                                )

                                Text(
                                    text = "Rain ${s.popPct}%   Wind ${s.windMph} mph",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun normalizeRoleKey(role: String): String {
    val trimmed = role.trim()
    if (trimmed.isBlank()) return "unknown"

    val normalized = trimmed
        .replace(' ', '_')
        .replace('-', '_')
        .lowercase(Locale.US)

    return normalized.replace(Regex("_+"), "_")
}

private fun prettyRoleFromKey(roleKey: String): String {
    val words = roleKey.split('_').filter { it.isNotBlank() }
    return words.joinToString(" ") { w ->
        w.replaceFirstChar { c -> c.uppercaseChar() }
    }
}

@Composable
private fun CenterText(inner: PaddingValues, text: String) {
    Box(
        modifier = Modifier
            .padding(inner)
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text)
    }
}

@Composable
private fun CenterLoading(inner: PaddingValues) {
    Box(
        modifier = Modifier
            .padding(inner)
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
            CircularProgressIndicator()
            Text("Loading forecast...")
        }
    }
}

@Composable
private fun ErrorBlock(inner: PaddingValues, message: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier
            .padding(inner)
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(message)
        Button(onClick = onRetry) { Text("Retry") }
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

private fun shiftStatus(roleKey: String, s: DaySummary): String {
    val cond = s.condition.lowercase(Locale.US)
    val wet = s.popPct >= 50 ||
            cond.contains("rain") ||
            cond.contains("storm") ||
            cond.contains("thunder") ||
            cond.contains("snow")

    return when (roleKey) {
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