//Nia Junod & Alina Tarasevich
package pridwin.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContextLogicScreen(onBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Context Logic") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                "How it works",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold
            )

            Text(
                "This app is a context-aware decision assistant for hospitality work. "
                        + "Instead of asking the user to manually interpret weather, it combines live environmental signals "
                        + "with the user’s selected role (pool vs beach vs main restaurant) to generate guidance that changes "
                        + "automatically as conditions change.",
                style = MaterialTheme.typography.bodyMedium
            )

            Card(modifier = Modifier.fillMaxWidth()) {
                Section("Context inputs") {
                    Bullet("Hardware sensor (implemented): Device location is used to fetch local weather and forecast.")
                    Bullet("Virtual sensor (implemented): Weather API provides temperature, conditions, precipitation probability, and wind.")
                    Bullet("Derived feature (implemented): Day/night is computed using sunrise/sunset timestamps from the weather response.")
                    Bullet("User-provided context (implemented): Role selection (e.g., Pool Server, Beach Server, Main Bartender) determines how weather is interpreted.")
                }
            }

            Card(modifier = Modifier.fillMaxWidth()) {
                Section("Context model: what the app tries to infer") {
                    Bullet("Outdoor suitability: Is it reasonable to staff outdoor-facing roles today?")
                    Bullet("Operational risk: Are there conditions (rain/wind/extreme temps) that increase safety or service risk?")
                    Bullet("Confidence level: When conditions are borderline, the app communicates uncertainty via TENTATIVE instead of a hard ON/OFF.")
                }
            }

            Card(modifier = Modifier.fillMaxWidth()) {
                Section("Decision logic: turning context into guidance") {
                    Bullet("Step 1 — Summarize each day: compute daily high/low temperature, max precipitation probability, average wind, and most common condition label.")
                    Bullet("Step 2 — Classify “wet” days: rain/storm/thunder/snow or precipitation probability above a threshold triggers a wet condition.")
                    Bullet("Step 3 — Apply role-specific rules: outdoor roles are more sensitive to wet/cold; indoor roles are more stable.")
                    Bullet("Output: each day is labeled ON / OFF / TENTATIVE for the selected role.")
                }
            }

            Card(modifier = Modifier.fillMaxWidth()) {
                Section("Role-based adaptation: why this is Ubicomp") {
                    Bullet("Same environment, different meaning: 70°F and sunny might be ON for Pool Server, but irrelevant to Main Bartender scheduling.")
                    Bullet("The UI adapts to your role: forecast is not presented as raw meteorology—it's presented as actionable staffing guidance.")
                    Bullet("Implicit interaction: the app reduces explicit input (no manual reasoning) by translating sensor data into recommendations.")
                }
            }

            Card(modifier = Modifier.fillMaxWidth()) {
                Section("Examples") {
                    Bullet("Rainy day + outdoor role (Pool/Beach) → OFF or TENTATIVE depending on severity.")
                    Bullet("Warm, dry day + Pool role → ON, and may show heat-focused reminders as a secondary cue.")
                    Bullet("Snow/very low temps + indoor role → still ON, but may show TENTATIVE if extreme lows increase travel risk.")
                }
            }

            Card(modifier = Modifier.fillMaxWidth()) {
                Section("What is stored") {
                    Bullet("Only settings are saved locally (role selection, theme, toggles).")
                    Bullet("Weather data is treated as ephemeral (used to compute the UI, then refreshed when needed).")
                    Bullet("No personal profiles are created from context signals.")
                }
            }

            Card(modifier = Modifier.fillMaxWidth()) {
                Section("Optional extension (if you implement it)") {
                    Bullet("Motion/activity inference: use accelerometer/activity recognition to infer commuting context (walking vs driving).")
                    Bullet("Background behavior: periodic checks (WorkManager) to alert the user when the context shifts (e.g., rain starting before a pool shift).")
                    Bullet("User control: extensions should be opt-in and disableable to support privacy-by-design.")
                }
            }

            Spacer(Modifier.height(6.dp))
            HorizontalDivider()
            Spacer(Modifier.height(2.dp))

            Text(
                "Summary",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                "The core ubicomp idea is role-aware context interpretation: the same sensor inputs produce different guidance "
                        + "depending on what the user is doing (their job role). The app prioritizes actionable recommendations over raw data, "
                        + "and communicates uncertainty when context is borderline.",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(Modifier.height(12.dp))
        }
    }
}

@Composable
private fun Section(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
        content()
    }
}

@Composable
private fun Bullet(text: String) {
    Text("• $text", style = MaterialTheme.typography.bodyMedium)
}